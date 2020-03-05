package com.intuit.quickfabric.commons.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import com.intuit.quickfabric.commons.vo.ClusterStep;
import com.intuit.quickfabric.commons.vo.ClusterVO;
import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;
import org.apache.commons.lang3.StringUtils;

public class ReportBuilder {

    private StringBuilder builder;

    public ReportBuilder() {
        this.builder = new StringBuilder();
    }

    public String build() {
        return builder.toString();
    }

    public ReportBuilder appendTimeRangeHeader(Timestamp from, Timestamp to) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        builder.append("<h4 align='center'>")
            .append("<i>")
            .append("This report shows metrics from ")
            .append(sdf.format(from))
            .append(" to ")
            .append(sdf.format(to))
            .append("</i>")
            .append("</h4>");

        return this;
    }

    public ReportBuilder appendRanAtHeader(Timestamp timestamp) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        builder.append("<h4 align='center'>")
            .append("<i>")
            .append("This report shows data as of ")
            .append(sdf.format(timestamp))
            .append("</i>")
            .append("</h4>");

        return this;
    }

    public ReportBuilder appendMetricsTable(List<EMRClusterMetricsVO> reportMetrics) {
        builder.append("<table border='3'>")
            .append("<tr>")
            .append("<th bgcolor=\"#368BC1\">EMR Name</th>")
            .append("<th bgcolor=\"#368BC1\">Account ID</th>")
            .append("<th bgcolor=\"#368BC1\">Cost (Current Month)</th>")
            .append("<th bgcolor=\"#368BC1\">Segment</th>")
            .append("<th bgcolor=\"#368BC1\">Status</th>")
            .append("<th bgcolor=\"#368BC1\">Total Apps Succeeded</th>")
            .append("<th bgcolor=\"#368BC1\">Total Apps Failed</th>")
            .append("<th bgcolor=\"#368BC1\">Avg Apps Running</th>")
            .append("<th bgcolor=\"#368BC1\">Avg Memory Usage %</th>")
            .append("</tr>");

        for (int i = 0; i < reportMetrics.size(); i++) {
            builder.append("<tr align='center'><td>")
                .append(reportMetrics.get(i).getEmrName())
                .append("</td><td>")
                .append(reportMetrics.get(i).getAccount())
                .append("</td><td style='color:#157DEC'>$")
                .append(reportMetrics.get(i).getCost())
                .append("</td><td>")
                .append(reportMetrics.get(i).getClusterSegment())
                .append("</td><td style='color:#387C44'><b>")
                .append("Healthy")
                .append("</b></td><td>")
                .append(reportMetrics.get(i).getAppsSucceeded())
                .append("</td><td>")
                .append(reportMetrics.get(i).getAppsFailed())
                .append("</td><td>")
                .append(reportMetrics.get(i).getAppsRunning())
                .append("</td><td>")
                .append(reportMetrics.get(i).getMemoryUsagePct())
                .append("</td></tr>");
        }
        
        builder.append("</table>");

        return this;
    }

    public ReportBuilder appendAMIReportTable(List<ClusterVO> clusters) {        
        builder.append("<table border='3'>")
            .append("<tr>")
            .append("<th bgcolor=\"#368BC1\">EMR Name</th>")
            .append("<th bgcolor=\"#368BC1\">Account ID</th>")
            .append("<th bgcolor=\"#368BC1\">Segment</th>")
            .append("<th bgcolor=\"#368BC1\">Status</th>")
            .append("<th bgcolor=\"#368BC1\">Creation Date</th>")
            .append("<th bgcolor=\"#368BC1\">Created By</th>")
            .append("<th bgcolor=\"#368BC1\">Days overdue for AMI Rotation</th>")
            .append("<th bgcolor=\"#368BC1\">In Violation</th>")
            .append("</tr>");

        for (ClusterVO cluster : clusters) {
            //Constructing template based on ami rotation days overdue
            if(cluster.getAMIRotationDaysToGo() < 0) {
                this.appendDaysOverDueRow(cluster);
            } else {
                this.appendDaysLeftRow(cluster);
            }
        }
        builder.append("</table>");

        return this;

    }

    public ReportBuilder openHtmlTag() {
        builder.append("<html>");
        return this;
    }

    public ReportBuilder openBodyTag() {
        builder.append("<body>");
        return this;
    }

    public ReportBuilder closeHtmlTag() {
        builder.append("</html>");
        return this;
    }

    public ReportBuilder closeBodyTag() {
        builder.append("</body>");
        return this;
    }

    private StringBuilder appendDaysOverDueRow(ClusterVO cluster) {
        String daysOverdue = Math.abs(cluster.getAMIRotationDaysToGo()) + " days Overdue";

        builder.append("<tr align='center'><td>")
            .append(cluster.getClusterName())
            .append("</td><td>")
            .append(cluster.getAccount())
            .append("</td><td>")
            .append(cluster.getSegment())
            .append("</td><td style='color:#387C44'><b>")
            .append("Healthy")
            .append("</b></td><td>")
            .append(cluster.getCreationTimestamp())
            .append("</td><td>")
            .append(cluster.getCreatedBy())
            .append("</td><td style='color:#FF3333'><b>")
            .append(daysOverdue).append("</b>")
            .append("</td><td style='color:#FF3333'><b>")
            .append("YES")
            .append("</b></td></tr>");

        return builder;
    }


    private StringBuilder appendDaysLeftRow(ClusterVO cluster) {
        String days = Math.abs(cluster.getAMIRotationDaysToGo()) + " days left";
        String marigold = "#EAA221";

        builder.append("<tr align='center'><td>")
            .append(cluster.getClusterName())
            .append("</td><td>")
            .append(cluster.getAccount())
            .append("</td><td>")
            .append(cluster.getSegment())
            .append("</td><td style='color:#387C44'><b>")
            .append("Healthy")
            .append("</b></td><td>")
            .append(cluster.getCreationTimestamp())
            .append("</td><td>")
            .append(cluster.getCreatedBy())
            .append("</td><td style='color:").append(marigold).append("'><b>")
            .append(days).append("</b>")
            .append("</td><td style='color:#7FFF00'><b>")
            .append("NO")
            .append("</b></td></tr>");

        return builder;
    }

    public ReportBuilder appendAMIReportHeader() {
        builder.append("<h1 align='center'>")
            .append("AMI Rotation Report")
            .append("</h1>");

        return this;

    }

    public ReportBuilder appendMetricsReportHeader() {
        builder.append("<h1 align='center'>")
            .append("Cluster Metrics Report")
            .append("</h1>");

        return this;
    }

    public ReportBuilder openAlignCenterDiv() {
        builder.append("<div align='center'>");
        return this;
    }

    public ReportBuilder closeDiv() {
        builder.append("</div>");
        return this;
    }

    public ReportBuilder appendClusterDetailsTable(ClusterVO cluster) {
        builder.append("<table border='3'>");
            
        builder.append("<tr>");
        
        if(!StringUtils.isBlank(cluster.getClusterId())) {
            builder.append("<th bgcolor=\"#368BC1\">").append("EMR ID").append("</th>");
        }
        
        builder.append("<th bgcolor=\"#368BC1\">").append("EMR Name").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Status").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Account").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Created By").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Creation Date").append("</th>");
            
        builder.append("</tr>");
        
        builder.append("<tr>");

        if(!StringUtils.isBlank(cluster.getClusterId())) {
            builder.append("<td>").append(cluster.getClusterId()).append("</td>");
        }

        builder.append("<td>").append(cluster.getClusterName()).append("</td>")
            .append("<td>").append(cluster.getStatus()).append("</td>")
            .append("<td>").append(cluster.getAccount()).append("</td>")
            .append("<td>").append(cluster.getCreatedBy()).append("</td>")
            .append("<td>").append(cluster.getCreationTimestamp()).append("</td>");

        builder.append("</tr>");
            
        builder.append("</table>");

        return this;
    }
    
    public ReportBuilder h3(String content) {
        builder.append("<h3>")
            .append(content)
            .append("</h3>");
        
        return this;
    }

    public ReportBuilder appendMetricsReport(List<EMRClusterMetricsVO> metrics, Timestamp from, Timestamp to) {
        this.appendMetricsReportHeader()
            .appendTimeRangeHeader(from, to)
            .openAlignCenterDiv()
            .appendMetricsTable(metrics)
            .closeDiv();

        return this;

    }

    public ReportBuilder appendAMIRotationReport(List<ClusterVO> clusters, Timestamp now) {
        this.appendAMIReportHeader()
            .appendRanAtHeader(now)
            .openAlignCenterDiv()
            .appendAMIReportTable(clusters)
            .closeDiv();

        return this;
    }

    public ReportBuilder appendStepsDetailsTable(List<ClusterStep> steps) {
        builder.append("<table border='3'>");
        
        builder.append("<tr>")
            .append("<th bgcolor=\"#368BC1\">").append("Step Name").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Status").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Action On Failure").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Jar").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Step Arg").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Main Class").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Created By").append("</th>")
            .append("<th bgcolor=\"#368BC1\">").append("Creation Date").append("</th>")
            .append("</tr>");
        
        for(ClusterStep step : steps) {
            builder.append("<tr>")
                .append("<td>").append(step.getName()).append("</td>")
                .append("<td>").append(step.getStatus()).append("</td>")
                .append("<td>").append(step.getActionOnFailure()).append("</td>")
                .append("<td>").append(step.getJar()).append("</td>")
                .append("<td>")
                .append(StringUtils.isBlank(step.getStepArg()) ? "N/A" : step.getStepArg())
                .append("</td>")
                .append("<td>")
                .append(StringUtils.isBlank(step.getMainClass()) ? "N/A" : step.getMainClass())
                .append("</td>")
                .append("<td>").append(step.getStepCreatedBy()).append("</td>")
                .append("<td>").append(step.getCreationTimestamp()).append("</td>")
                .append("</tr>");
        }
        
        builder.append("</table>");

        return this;
    }

}
