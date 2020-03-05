package com.intuit.quickfabric.emr.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.EMRClusterHealthTestCase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestSuiteDefinitionMapper implements ResultSetExtractor<List<EMRClusterHealthTestCase>> {

    @Override
    public List<EMRClusterHealthTestCase> extractData(ResultSet resultSet) throws SQLException, DataAccessException {

        List<EMRClusterHealthTestCase> testCases = new ArrayList<>();

        while(resultSet.next()){
            EMRClusterHealthTestCase testCase = new EMRClusterHealthTestCase();

            testCase.setId(resultSet.getInt("id"));
            testCase.setTestName(resultSet.getString("name"));
            testCase.setClusterSegment(resultSet.getString("cluster_segment"));
            testCase.setClusterType(resultSet.getString("cluster_type"));
            testCase.setTestCriteria(resultSet.getString("criteria"));
            testCase.setExpiresInMinutes(resultSet.getInt("expires_minutes"));
            testCase.setMandatory(resultSet.getBoolean("mandatory"));
            testCase.setDisabled(resultSet.getBoolean("disabled"));

            testCases.add(testCase);
        }

        return  testCases;
    }
}