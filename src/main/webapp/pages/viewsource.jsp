<%@page contentType="text/html;charset=UTF-8" import="java.util.*,java.io.*,eionet.cr.dao.DAOFactory"%>

<%@ include file="/pages/common/taglibs.jsp"%>	

<%@ page import="eionet.cr.dto.HarvestBaseDTO" %>
<%@ page import="eionet.cr.harvest.Harvest" %>

<stripes:layout-render name="/pages/common/template.jsp" pageTitle="View Harvesting Source">
	<stripes:layout-component name="errors"/>
	<stripes:layout-component name="messages"/>
	<stripes:layout-component name="contents">
		<h1>View source</h1>
		<br/>
	    <stripes:form action="/source.action" focus="">
	    	<stripes:hidden name="harvestSource.sourceId"/>
	        <table>
	            <tr>
	                <td>Name:</td>
	                <td>${actionBean.harvestSource.name}</td>
	            </tr>
	            <tr>
	                <td>URL:</td>
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
	                <td>Number of resources:</td>
	                <td>
	                	${actionBean.harvestSource.resources}
	                </td>
	            </tr>
	            <tr>
	                <td>Schedule:</td>
	                <td>
	                	weekday: 
	                	${actionBean.harvestSource.harvestSchedule.weekday}&nbsp;&nbsp;&nbsp; 
	                	hour: 
	                	${actionBean.harvestSource.harvestSchedule.hour}&nbsp;&nbsp;&nbsp;
	                	period (weeks): 
	                	${actionBean.harvestSource.harvestSchedule.period}
	                </td>
	            </tr>
	            <tr>
	                <td colspan="2" style="padding-top:10px">
	                	<stripes:submit name="harvestNow" value="Harvest now"/>
	                    <stripes:submit name="scheduleImmediateHarvest" value="Schedule for immediate harvest"/>
	                </td>
	            </tr>
	        </table>
	        <br/><br/>
	        <strong>Last 10 harvests:</strong>
	        <table class="datatable">	        	
	        	<thead>
		        	<tr>
		        		<th scope="col">Type</th>
		        		<th scope="col">User</th>
		        		<th scope="col">Started</th>
		        		<th scope="col">Finished</th>
		        		<th scope="col">Resources</th>
		        		<th scope="col">Encoding schemes</th>
		        		<th scope="col">Statements</th>
		        		<th scope="col"></th>
		        	</tr>
	        	</thead>
	        	<tbody>
	        		<c:forEach items="${actionBean.harvests}" var="harv" varStatus="loop">
	        			<tr>
	        				<td>${harv.harvestType}</td>
	        				<td>${harv.user}</td>
	        				<td><fmt:formatDate value="${harv.datetimeStarted}" pattern="dd-MM-yy HH:mm:ss"/></td>
	        				<td><fmt:formatDate value="${harv.datetimeFinished}" pattern="dd-MM-yy HH:mm:ss"/></td>		        				
	        				<td>${harv.totalResources}</td>
	        				<td>${harv.encodingSchemes}</td>
	        				<td>${harv.totalStatements}</td>
							<td>
		        				<stripes:link href="/harvest.action">
									<c:if test="${(!(empty harv.hasFatals) && harv.hasFatals) || (!(empty harv.hasErrors) && harv.hasErrors)}">
										<img src="${pageContext.request.contextPath}/images/error.gif" title="Errors"/>
									</c:if>
									<c:if test="${!(empty harv.hasWarnings) && harv.hasWarnings}">
										<img src="${pageContext.request.contextPath}/images/warning.gif" title="Warnings"/>
									</c:if>
	                                <img src="${pageContext.request.contextPath}/images/view2.gif" title="View"/>
	                                <stripes:param name="harvestDTO.harvestId" value="${harv.harvestId}"/>
	                            </stripes:link>
		        			</td>	        				
	        			</tr>
	        		</c:forEach>
	        	</tbody>
	        </table>
	    </stripes:form>

	</stripes:layout-component>
</stripes:layout-render>
