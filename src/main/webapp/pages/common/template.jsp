<%@ include file="/pages/common/taglibs.jsp"%>
<stripes:layout-definition>
	<c:choose>
		<c:when test="${not empty initParam.templateJsp && !fn:contains(initParam.templateJsp, '${')}">
    		<stripes:layout-render name="/pages/common/${initParam.templateJsp}" pageTitle="${pageTitle}"/>
	    </c:when>
    	<c:otherwise>
			<stripes:layout-render name="/pages/common/templateEionet.jsp" pageTitle="${pageTitle}"/>
		</c:otherwise>
	</c:choose>
</stripes:layout-definition>