<%@page contentType="text/html;charset=UTF-8"%>

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
					</li>
				</ul>
			</div>
						      			
			<h1>Harvesting sources</h1>
			<p></p>
			<div id="searchForm" style="padding-bottom: 5px">
				<stripes:label for="filterString" class="question">Filter expression</stripes:label>
				<stripes:text name="searchString" id="filterString"/>
				<stripes:hidden name="type" value="${actionBean.type }"/>
				<stripes:submit name="view" value="Filter" />
				<c:if test="${!empty actionBean.searchString }"> 
					<stripes:link href="${actionBean.urlBinding}?view">
						<stripes:link-param name="type" value="${actionBean.type }"/>
						<img src="${pageContext.request.contextPath}/images/delete_small.gif" title="Remove filter" alt="Remove filter"/>
					</stripes:link>
				</c:if>
			</div>
			<div id="tabbedmenu">
			    <ul>
			    	<c:forEach items="${actionBean.sourceTypes}" var="loopItem">
						<c:choose>
					  		<c:when test="${actionBean.type==loopItem.type}" > 
								<li id="currenttab"><span><c:out value="${loopItem.title}"/></span></li>
							</c:when>
							<c:otherwise>
								<li>
									<stripes:link href="${actionBean.urlBinding}">
										<c:out value="${loopItem.title}"/>
						                <stripes:param name="type" value="${loopItem.type}"/>
						                <stripes:param name="searchString" value="${actionBean.searchString }"/>
						            </stripes:link>
					            </li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
			    </ul>
			</div>
			<br style="clear:left" />
			<div style="margin-top:20px;margin-bottom:5px">	
				<display:table name="${actionBean.harvestSources}" class="sortable" pagesize="15"
								sort="list" id="harvestSource" htmlId="harvestSources" requestURI="${actionBean.pagingUrl}"
								decorator="eionet.cr.web.util.HarvestSourcesTableDecorator" style="width:100%"
								excludedParams="harvest delete sourceUrl view">
					<display:setProperty name="paging.banner.items_name" value="sources"/>
					<display:column>
						<input type="checkbox" name="sourceUrl" value="${harvestSource.url}"/>
					</display:column>
					<display:column property="url" title="URL" sortable="true"/>
					<display:column property="lastHarvest" title="Last harvest" sortable="true"/>
				</display:table>				
			</div>
			<div>
				<stripes:submit name="delete" value="Delete" title="Delete selecetd sources"/>
				<stripes:submit name="harvest" value="Schedule urgent harvest" title="Schedule urgent harvest of selecetd sources"/>
				<input type="button" name="selectAll" value="Select all" onclick="toggleSelectAll('generalForm');return false"/>
			</div>
		</stripes:form>                  
	</stripes:layout-component>
</stripes:layout-render>
