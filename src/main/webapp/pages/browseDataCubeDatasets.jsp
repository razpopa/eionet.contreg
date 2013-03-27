<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/pages/common/taglibs.jsp"%>

<stripes:layout-render name="/pages/common/template.jsp" pageTitle="Browse DataCube datasets">

    <stripes:layout-component name="head">
        <script type="text/javascript">
        // <![CDATA[
        // ]]>
        </script>
    </stripes:layout-component>

    <stripes:layout-component name="contents">

    <h1>Browse DataCube datasets</h1>

    <p>
        This page enables you to browse DataCube datasets available in the system.<br/>
        It simply lists all datasets found, and provides paging functions if there is more than <c:out value="${actionBean != null && actionBean.datasets != null ? actionBean.datasets.objectsPerPage : 20}"/> DataCube datasets in the system.<br/>
        Clicking on a listed dataset leads to the detailed view page of its metadata which is further browseable.
    </p>

    <div style="margin-top:20px;width:100%">
        <display:table name="${actionBean.datasets}" id="dataset" class="sortable" sort="external" requestURI="${actionBean.urlBinding}" style="width:100%">

            <display:setProperty name="paging.banner.item_name" value="dataset"/>
            <display:setProperty name="paging.banner.items_name" value="datasets"/>
            <display:setProperty name="paging.banner.all_items_found" value='<div class="pagebanner">{0} {1} found.</div>'/>
            <display:setProperty name="paging.banner.onepage" value=""/>

            <c:forEach items="${actionBean.availColumns}" var="column" varStatus="columnsLoopStatus">

                <display:column title="${column.title}" sortable="${column.sortable}" sortProperty="${column.alias}" style="width:${column.width}">

                    <c:if test="${not empty column.isFactsheetLink}">
                        <stripes:link beanclass="${actionBean.factsheetActionBeanClass.name}">
                            <c:out value="${dataset.left}"/>
                            <stripes:param name="uri" value="${dataset.left}"/>
                        </stripes:link>
                    </c:if>
                    <c:if test="${empty column.isFactsheetLink}">
                        <c:out value="${dataset.right}"/>
	                </c:if>

                </display:column>

            </c:forEach>

        </display:table>
    </div>

</stripes:layout-component>
</stripes:layout-render>
