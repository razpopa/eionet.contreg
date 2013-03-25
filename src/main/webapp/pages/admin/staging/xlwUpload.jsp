<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/pages/common/taglibs.jsp"%>

<stripes:layout-render name="/pages/common/template.jsp" pageTitle="Upload a spreadsheet">

    <stripes:layout-component name="contents">

        <%-- The page's heading --%>

        <h1>Upload a spreadsheet</h1>

        <div style="margin-top:20px">
            This page enables you to upload an MS Excel or OpenDocument spreadsheet into CR's triple store.<br/>
            Only files of certain type of content are supported, meaning that CR knows how to map these into the triple store.<br/>
            You must specify one of these types below and upload a spreadsheet file from your computer.<br/>
            If the file is not a supported spreadsheet file or there is a problem with mapping into the triple store,<br/>
            the system returns a relevant error message and rolls back any changes made!
        </div>

        <%-- The form --%>

        <div style="padding-top:20px">
            <crfn:form id="uploadForm" beanclass="${actionBean.class.name}" method="post">

                <table>
                    <tr>
                        <td style="text-align:right">
                            <label for="selContentType" class="question required">Content type:</label>
                        </td>
                        <td>
                            <stripes:select id="selContentType" name="uploadType" title="${actionBean.uploadType.hint}">
                                <c:forEach items="${actionBean.uploadTypes}" var="uploadType">
                                    <stripes:option value="${uploadType.name}" label="${uploadType.title}" title="${uploadType.hint}"/>
                                </c:forEach>
                            </stripes:select>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align:right">
                            <label for="fileInput" class="question required">Spreadsheet file:</label>
                        </td>
                        <td>
                           <stripes:file name="fileBean" id="fileInput" size="120"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                           &nbsp;
                        </td>
                        <td style="padding-top:10px">
                            <stripes:submit name="upload" value="Upload"/>
                            <stripes:submit name="cancel" value="Cancel"/>
                        </td>
                    </tr>
                </table>

                <c:if test="${not empty actionBean.uploadedGraphUri}">
                    <div class="tip-msg">
                        <strong>Tip</strong>
                        <p>
                            The objects were imported into the following graph. Please click on this link to explore its contents:<br/>
                            <stripes:link beanclass="${actionBean.objectsInSourceActionBeanClass.name}">
                                <stripes:param name="search" value=""/>
                                <stripes:param name="uri" value="${actionBean.uploadedGraphUri}"/>
                                <c:out value="${actionBean.uploadedGraphUri}"/>
                            </stripes:link>
                        </p>
                    </div>
                </c:if>

            </crfn:form>
        </div>

    </stripes:layout-component>
</stripes:layout-render>
