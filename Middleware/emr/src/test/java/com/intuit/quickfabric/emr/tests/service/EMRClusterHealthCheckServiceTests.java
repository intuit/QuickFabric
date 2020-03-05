package com.intuit.quickfabric.emr.tests.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.quickfabric.commons.vo.ClusterHealthCheckStatusUpdate;
import com.intuit.quickfabric.emr.dao.EMRClusterMetadataDao;
import com.intuit.quickfabric.emr.helper.EMRClusterHealthHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class EMRClusterHealthCheckServiceTests {

    @MockBean
    EMRClusterHealthHelper emrClusterHealthHelper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    EMRClusterMetadataDao metadataDao;

    @Autowired
    ObjectMapper objectMapper;

    private MockMvc mockMvc;

    final String fakeClusterId = "CLUSTER-123";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        mockMvc = MockMvcBuilders.standaloneSetup(healthCheckService).build();
    }

    @Test
    @WithMockUser(authorities = "fakeRole")
    public void getAccessDeniedExceptionIfUserDoesNotHaveExpectedRole() {

        ResponseEntity response = new ResponseEntity(HttpStatus.OK);
        when(emrClusterHealthHelper.getEMRTestSuites(any(), any())).thenReturn(new ArrayList<>());
        assertThatThrownBy(() ->
                mockMvc.perform(MockMvcRequestBuilders.get("/emr/health/test-suites")
                        .param("clusterType", "someType")
                        .param("clusterSegment", "someSegment")
                        .accept(MediaType.APPLICATION_JSON))
        ).hasCause(new AccessDeniedException("Access is denied"));
    }

    @Test
    @WithMockUser(authorities = "admin")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testSuitesReturnsOk() throws Exception {
        ResponseEntity response = new ResponseEntity(HttpStatus.OK);
        when(emrClusterHealthHelper.getEMRTestSuites(any(), any())).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.get("/emr/health/test-suites")
                .param("clusterType", "someType")
                .param("clusterSegment", "someSegment")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(emrClusterHealthHelper, times(1)).getEMRTestSuites(any(), any());
    }

    @Test
    @WithMockUser(authorities = "admin")
    public void return405IfMethodIsNotGet() throws Exception {
        ResponseEntity response = new ResponseEntity(HttpStatus.OK);
        when(emrClusterHealthHelper.getEMRTestSuites(any(), any())).thenReturn(new ArrayList<>());

        mockMvc.perform(MockMvcRequestBuilders.post("/emr/health/test-suites")
                .param("clusterType", "someType")
                .param("clusterSegment", "someSegment")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(MockMvcRequestBuilders.put("/emr/health/test-suites")
                .param("clusterType", "someType")
                .param("clusterSegment", "someSegment")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(MockMvcRequestBuilders.delete("/emr/health/test-suites")
                .param("clusterType", "someType")
                .param("clusterSegment", "someSegment")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser(authorities = "admin")
    public void verifyUpdateEMRClusterHealthTestIsCalled() throws Exception {
        ResponseEntity response = new ResponseEntity(HttpStatus.OK);
        ClusterHealthCheckStatusUpdate statusUpdate = new ClusterHealthCheckStatusUpdate();
        statusUpdate.setExecutionId(123);
        statusUpdate.setStatus("success");
        when(emrClusterHealthHelper.updateEMRClusterHealthTest(fakeClusterId, statusUpdate)).thenReturn("success");

        String json = objectMapper.writeValueAsString(statusUpdate);
        mockMvc.perform(MockMvcRequestBuilders.put("/emr/health/test/update/" + fakeClusterId)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk());

        verify(emrClusterHealthHelper, times(1)).updateEMRClusterHealthTest(anyString(), any(ClusterHealthCheckStatusUpdate.class));
    }

    @Test
    @WithMockUser(authorities = "admin")
    public void missingStatusFieldInRequestReturnsException() throws Exception {
        ClusterHealthCheckStatusUpdate statusUpdate = new ClusterHealthCheckStatusUpdate();
        statusUpdate.setExecutionId(123);

        String json = objectMapper.writeValueAsString(statusUpdate);
        Exception resolvedException = mockMvc.perform(MockMvcRequestBuilders.put("/emr/health/test/update/" + fakeClusterId)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }
}
