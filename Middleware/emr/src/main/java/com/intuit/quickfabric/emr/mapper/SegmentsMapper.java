package com.intuit.quickfabric.emr.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.SegmentVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SegmentsMapper implements ResultSetExtractor<List<SegmentVO>> {

    public List<SegmentVO> extractData(ResultSet rs) throws SQLException, DataAccessException {

        List<SegmentVO> segments = new ArrayList<>();
        while (rs.next()) {
            SegmentVO segment = new SegmentVO();
            segment.setSegmentId(rs.getInt("segment_id"));
            segment.setSegmentName(rs.getString("segment_name"));
            segment.setBusinessOwner(rs.getString("business_owner"));
            segment.setBusinessOwnerEmail(rs.getString("business_owner_email"));
            segments.add(segment);
        }
        return segments;
    }
}
