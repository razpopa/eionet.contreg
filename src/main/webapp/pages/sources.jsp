<%@page contentType="text/html;charset=UTF-8" import="java.util.*,java.io.*"%>

<%@ include file="/pages/common/taglibs.jsp"%>	

<stripes:layout-render name="/pages/common/template.jsp" pageTitle="Harvesting Sources">

	<stripes:layout-component name="errors"/>
	<stripes:layout-component name="messages"/>
	
	<stripes:layout-component name="contents">
		<stripes:form id="generalForm" action="${actionBean.urlBinding}">	
			<div id="operations">
				<ul>
					<li>
						<stripes:link href="/source.action" event="add">Add new source</stripes:link>
						<c:if test="${actionBean.type!=null && actionBean.type=='data'}">
							<stripes:link href="${actionBean.urlBinding}" event="harvest" title="Schedule urgent harvest of all data sources">
								Harvest all data
								<stripes:param name="type" value="${actionBean.type}"/>
							</stripes:link>
						</c:if>
						<c:if test="${actionBean.type!=null && actionBean.type=='schema'}">
							<stripes:link href="${actionBean.urlBinding}" event="harvest" title="Schedule urgent harvest of all schemas sources">
								Harvest all schemas
								<stripes:param name="type" value="${actionBean.type}"/>
							</stripes:link>					
						</c:if>
					</li>
				</ul>
			</div>
			      			
			<h1>Harvesting sources</h1>
			<p></p>
			<div id="tabbedmenu">
			    <ul>
			    	<c:forEach items="${actionBean.sourceTypes}" var="loopItem">
						<c:choose>
					  		<c:when test="${actionBean.type==loopItem.type}" > 
								<li id="currenttab"><span>${fn:escapeXml(loopItem.title)}</span></li>
							</c:when>
							<c:otherwise>
								<li>
									<stripes:link href="${actionBean.urlBinding}">
										${fn:escapeXml(loopItem.title)}
						                <stripes:param name="type" value="${loopItem.type}"/>
						            </stripes:link>
					            </li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
			    </ul>
			</div>
			<br style="clear:left" />
			<div style="margin-top:20px;margin-bottom:5px">	
				<display:table name="${actionBean.harvestSources}" class="sortable" pagesize="15" sort="list" id="harvestSource" htmlId="harvestSources" requestURI="${actionBean.urlBinding}" decorator="eionet.cr.web.util.HarvestSourcesTableDecorator" style="width:100%">
					<display:setProperty name="paging.banner.items_name" value="sources"/>
					<display:column>
						<input type="checkbox" name="sourceUrl" value="${harvestSource.url}"/>
					</display:column>
					<display:column property="url" title="URL" sortable="true"/>
				</display:table>				
			</div>
			<div>
				<stripes:submit name="delete" value="Delete" title="Delete selecetd sources"/>
				<stripes:submit name="harvest" value="Harvest" title="Harvest selecetd sources"/>
				<input type="button" name="selectAll" value="Select all" onclick="toggleSelectAll'generalForm');return false"/>
			</div>
		</stripes:form>                  
	</stripes:layout-component>
</stripes:layout-render>
