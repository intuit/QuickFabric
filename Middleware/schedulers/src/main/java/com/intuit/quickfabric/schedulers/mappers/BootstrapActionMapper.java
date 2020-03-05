package com.intuit.quickfabric.schedulers.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.BootstrapActionVO;

public class BootstrapActionMapper implements ResultSetExtractor<List<BootstrapActionVO>> {
    public List<BootstrapActionVO> extractData(ResultSet rs) throws SQLException,
    DataAccessException {

        List<BootstrapActionVO> vo = new ArrayList<BootstrapActionVO>();
        while (rs.next()) { 

            BootstrapActionVO bootstrapAction= new BootstrapActionVO();

            bootstrapAction.setBootstrapName(rs.getString("name"));
            bootstrapAction.setBootstrapScript(rs.getString("step_arg"));
            vo.add(bootstrapAction);

        }

        return vo;
    }

}
