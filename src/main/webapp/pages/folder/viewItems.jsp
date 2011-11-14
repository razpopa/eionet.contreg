<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/pages/common/taglibs.jsp"%>

<stripes:layout-render name="/pages/common/template.jsp" pageTitle="Resource properties">

    <stripes:layout-component name="head">
        <script type="text/javascript">
        // <![CDATA[
            ( function($) {
                $(document).ready(
                    function(){

                        // Open delete bookmarked queries dialog
                        $("#createFolderLink").click(function() {
                            $('#createFolderDialog').dialog('open');
                            return false;
                        });

                        // Delete bookmarked queries dialog setup
                        $('#createFolderDialog').dialog({
                            autoOpen: false,
                            width: 500
                        });

                        // Close dialog
                        $("#closeFolderDialog").click(function() {
                            $('#createFolderDialog').dialog("close");
                            return true;
                        });
                    });
            } ) ( jQuery );
        // ]]>
        </script>
    </stripes:layout-component>

    <stripes:layout-component name="contents">

        <cr:tabMenu tabs="${actionBean.tabs}" />

        <br style="clear:left" />

        <div style="margin-top:20px">

            <c:if test="${actionBean.usersFolder}">
            <ul id="dropdown-operations">
                <li><a href="#">Operations</a>
                    <ul>
                        <li>
                            <a href="#" id="createFolderLink">Create folder</a>
                        </li>
                        <li>
                            <stripes:link href="/folder.action" event="uploadForm">
                                <stripes:param name="uri" value="${actionBean.uri}"/>
                                Upload file
                            </stripes:link>
                        </li>
                        <li>
                            <stripes:link href="/uploadCSV.action">
                                <stripes:param name="uri" value="${actionBean.uri}"/>
                                Upload CSV/TSV file
                            </stripes:link>
                        </li>
                        <li>
                            <stripes:link class="link-plain" href="/factsheet.action?edit=&uri=${actionBean.uri}" title="Edit folder properties">
                            Edit folder
                            </stripes:link>
                        </li>
                    </ul>
                </li>
            </ul>
            </c:if>

            <h1>
                <c:choose>
                    <c:when test="${actionBean.homeFolder}">
                        Home folder (${actionBean.folder.name})
                    </c:when>
                    <c:otherwise>
                        ${actionBean.folder.name}
                        <c:if test="${not empty actionBean.folder.title}">
                            (${actionBean.folder.title})
                        </c:if>
                        <c:url var="upIconUrl" value="images/move_up.gif" />
                        <stripes:link href="/folder.action" title="Move to parent folder" style="background: none">
                            <stripes:param name="uri" value="${actionBean.parentUri}"/>
                            <img src="${upIconUrl}" border="0" />
                        </stripes:link>
                    </c:otherwise>
                </c:choose>
            </h1>

            <crfn:form id="uploadsForm" action="/folder.action" method="post">
                <stripes:hidden name="uri" value="${actionBean.uri}" />
                <table>
                    <tbody>
                    <c:forEach var="item" items="${actionBean.folderItems}" varStatus="loop">
                        <stripes:hidden name="selectedItems[${loop.index}].uri" value="${item.uri}" />
                        <stripes:hidden name="selectedItems[${loop.index}].type" value="${item.type}" />
                        <stripes:hidden name="selectedItems[${loop.index}].name" value="${item.name}" />

                        <c:choose>
                            <c:when test="${item.folder || item.reservedFolder}">
                                <c:set var="cssClass" value="folder" />
                            </c:when>
                            <c:otherwise>
                                <c:set var="cssClass" value="file" />
                            </c:otherwise>
                        </c:choose>

                        <tr>
                            <c:if test="${actionBean.usersFolder}">
                                <c:set var="disabled" value="${item.reservedFolder || item.reservedFile}" />
                                <td><stripes:checkbox name="selectedItems[${loop.index}].selected" disabled="${disabled}" /></td>
                            </c:if>
                            <td class="${cssClass}" style="width: 100%">
                                <c:choose>
                                    <c:when test="${item.file || item.reservedFile}">
                                        <stripes:link href="factsheet.action">
                                            <stripes:param name="uri" value="${item.uri}"/>
                                            ${item.name}
                                        </stripes:link>
                                        <c:if test="${not empty item.title}">
                                            (${item.title})
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <stripes:link href="folder.action">
                                            <stripes:param name="uri" value="${item.uri}"/>
                                            ${item.name}
                                        </stripes:link>
                                        <c:if test="${not empty item.title}">
                                            (${item.title})
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                ${item.lastModified}
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

                <c:if test="${actionBean.usersFolder}">
                    <br />
                    <div>
                        <stripes:submit name="delete" value="Delete" title="Delete selecetd files"/>
                        <stripes:submit name="renameForm" value="Rename" title="Rename selecetd file"/>
                        <input type="button" name="selectAll" value="Select all" onclick="toggleSelectAll('uploadsForm');return false"/>
                    </div>
                </c:if>

            </crfn:form>

        </div>

        <%-- Add folder dialog --%>
        <div id="createFolderDialog" title="Create new folder">
            <crfn:form action="/folder.action" method="post">
                <stripes:hidden name="uri" value="${actionBean.uri}" />
                <fieldset style="border: 0px;">
                    <label for="txtTitle" style="width: 200px; float: left;">New folder name*:</label>
                    <stripes:text id="txtTitle" name="title"/>
                </fieldset>
                <fieldset style="border: 0px;">
                    <label for="txtLabel" style="width: 200px; float: left;">Short description:</label>
                    <stripes:text id="txtLabel" name="label"/>
                </fieldset>
                <br />
                <br />
                <stripes:submit name="createFolder" value="Create" title="Create new folder"/>
                <button id="closeFolderDialog">Cancel</button>
            </crfn:form>
        </div>

    </stripes:layout-component>

</stripes:layout-render>