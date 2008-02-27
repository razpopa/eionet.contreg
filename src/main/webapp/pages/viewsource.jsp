<%@page contentType="text/html;charset=UTF-8" import="java.util.*,java.io.*,eionet.cr.dao.DAOFactory"%>

<%@ include file="/pages/common/taglibs.jsp"%>	

<stripes:layout-render name="/pages/common/template.jsp" pageTitle="View Harvesting Source">
	<stripes:layout-component name="errors"/>
	<stripes:layout-component name="messages"/>
	<stripes:layout-component name="contents">
		<h1>View source</h1>
	    <stripes:form action="/source.action" focus="">
	    	<stripes:hidden name="harvestSource.sourceId"/>
	        <table>
	            <tr>
	                <td>Identifier:</td>
	                <td>${actionBean.harvestSource.identifier}</td>
	            </tr>
	            <tr>
	                <td>Pull URL:</td>
	                <td>${actionBean.harvestSource.url}</td>
	            </tr>
	            <tr>
	                <td>Type:</td>
	                <td>
	                	${actionBean.harvestSource.type}
	                </td>
	            </tr>
	            <tr>
	                <td>E-mails:</td>
	                <td>${actionBean.harvestSource.emails}</td>
	            </tr>
	            <tr>
	                <td>Date created:</td>
	                <td>
	                	${actionBean.harvestSource.dateCreated}
	                </td>
	            </tr>
	            <tr>
	                <td>Creator:</td>
	                <td>
	                	${actionBean.harvestSource.creator}
	                </td>
	            </tr>
	            <tr>
	                <td>Statements harvested:</td>
	                <td>
	                	${actionBean.harvestSource.statements}
	                </td>
	            </tr>
	            <tr>
	                <td>Schedule:</td>
	                <td>
	                	weekday: 
	                	${actionBean.harvestSource.harvestSchedule.weekday}<br/> 
	                	hour: 
	                	${actionBean.harvestSource.harvestSchedule.hour} <br/>
	                	period (weeks): 
	                	${actionBean.harvestSource.harvestSchedule.period}
	                </td>
	            </tr>
	            <tr>
	                <td colspan="2">
	                	<stripes:submit name="harvestNow" value="Harvest now"/>
	                    <stripes:submit name="scheduleImmediateHarvest" value="Schedule for immediate harvest"/>
	                    <!--stripes:submit name="push" value="Push from local file"/-->
	                </td>
	            </tr>
	        </table>
	        <br/>
	        Last 10 harvests:
	        <table class="datatable">
	        	<thead>
		        	<tr>
		        		<th scope="col">Nr</th>
		        		<th scope="col">Status</th>
		        		<th scope="col">Type</th>
		        		<th scope="col">User</th>
		        		<th scope="col">Started</th>
		        		<th scope="col">Finished</th>
		        		<th scope="col">Total statements</th>
		        		<th scope="col">Messages</th>
		        	</tr>
	        	</thead>
	        	<tbody>
	        		<c:forEach items="${actionBean.harvests}" var="harvest" varStatus="loop">
	        			<tr>
	        				<td>${loop.index + 1}</td>
	        				<td>${harvest.status}</td>
	        				<td>${harvest.harvestType}</td>
	        				<td>${harvest.user}</td>
	        				<td>${harvest.datetimeStarted}</td>
	        				<td>${harvest.datetimeFinished}</td>
	        				<td>${harvest.totalStatements}</td>
	        				<td>${harvest.messages}</td>
	        			</tr>
	        		</c:forEach>
	        	</tbody>
	        </table>
	    </stripes:form>

	</stripes:layout-component>
</stripes:layout-render>
