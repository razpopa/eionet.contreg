<%@page contentType="text/html;charset=UTF-8"%>

<%@ include file="/pages/common/taglibs.jsp"%>

<stripes:layout-render name="/pages/common/template.jsp" pageTitle="Resource properties">

    <stripes:layout-component name="contents">

        <cr:tabMenu tabs="${actionBean.tabs}" />
        <br />
        <br />
        <h1>View source</h1>

        <c:if test="${actionBean.harvestSource.permanentError}">
            <div class="warning-msg"><c:out value="The source is marked with permanent error!"/></div>
        </c:if>
        <c:if test="${actionBean.harvestSource.unavailable}">
            <div class="warning-msg"><c:out value="The source has been unavailable for too many times!"/></div>
        </c:if>


        <table class="datatable">
            <tr>
                <th scope="row">URL</th>
                <td>
                    <stripes:link href="/factsheet.action">
                        <c:out value="${actionBean.harvestSource.url}"/>
                        <stripes:param name="uri" value="${actionBean.harvestSource.url}"/>
                    </stripes:link>
                </td>
            </tr>
            <c:if test="${not empty actionBean.harvestSource.emails}">
                <tr>
                    <th scope="row">E-mails</th>
                    <td><c:out value="${actionBean.harvestSource.emails}" /></td>
                </tr>
            </c:if>
            <tr>
                <th scope="row">Owner</th>
                <td><c:out value="${actionBean.harvestSource.owner}" /></td>
            </tr>
            <tr>
                <th scope="row">Date created</th>
                <td><fmt:formatDate value="${actionBean.harvestSource.timeCreated}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
            <tr>
                <th scope="row">Harvest interval</th>
                <td>
                    <c:choose>
                        <c:when test="${empty actionBean.harvestSource.intervalMinutes || actionBean.harvestSource.intervalMinutes<=0}">
                            <c:out value="not to be batch-harvested"/>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${actionBean.intervalMinutesDisplay}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th scope="row">Last harvest</th>
                <td>
                    <c:choose>
                        <c:when test="${empty actionBean.harvestSource.lastHarvest}">
                            <c:out value="hasn't been harvested yet"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:formatDate value="${actionBean.harvestSource.lastHarvest}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            <c:if test="${actionBean.harvestSource.lastHarvestFailed && not actionBean.harvestSource.permanentError}">
                                <span style="color:red"><c:out value="(last harvest failed!)"/></span>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th scope="row">Statements</th>
                <td><c:out value="${actionBean.harvestSource.statements}"/></td>
            </tr>
            <c:if test="${not empty actionBean.harvestSource.intervalMinutes && actionBean.harvestSource.intervalMinutes>0}">
                <tr>
                    <th scope="row">Urgency score</th>
                    <td>
                        <c:choose>
                            <c:when test="${actionBean.harvestSource.harvestUrgencyScore<=0}">
                                <c:out value="cannot be calculated"/>
                            </c:when>
                            <c:otherwise>
                                <fmt:formatNumber value="${actionBean.harvestSource.harvestUrgencyScore}" pattern="#.####"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:if>
            <c:if test="${not empty actionBean.harvestSource.mediaType}">
                <tr>
                    <th scope="row">Media type</th>
                    <td><c:out value="${actionBean.harvestSource.mediaType}"/></td>
                </tr>
            </c:if>
            <tr>
                <th scope="row">"Schema" source</th>
                <td>
                    <c:choose>
                        <c:when test="${actionBean.schemaSource}">
                            <c:out value="yes"/>
                        </c:when>
                        <c:otherwise>
                            <c:out value="no"/>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <th scope="row">"Priority" source</th>
                <td>
                    <c:choose>
                        <c:when test="${actionBean.harvestSource.prioritySource}">
                            <c:out value="yes"/>
                        </c:when>
                        <c:otherwise>
                            <c:out value="no"/>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>

            <tr>
                <td colspan="2" style="padding-top: 10px">
                    <c:if test='${crfn:userHasPermission(pageContext.session, "/registrations", "u")}'>
                        <crfn:form action="/sourceView.action">
                            <stripes:hidden name="uri" />
                            <stripes:submit name="scheduleUrgentHarvest" value="Schedule urgent harvest" />
                        </crfn:form>
                    </c:if>
                </td>
            </tr>

        </table>

        <%-- Harvest history --%>
        <c:choose>
            <c:when test="${not empty actionBean.harvests}">
                <table class="datatable">
                    <caption>Last 10 harvests:</caption>
                    <thead>
                        <tr>
                            <th scope="col">Type</th>
                            <th scope="col">User</th>
                            <th scope="col">Started</th>
                            <th scope="col">Finished</th>
                            <th scope="col">Triples</th>
                            <th scope="col">Duration</th>
                            <th scope="col">Response Code</th>
                            <th scope="col"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${actionBean.harvests}" var="harv"
                            varStatus="loop">
                            <tr>
                                <td><c:out value="${harv.harvestType}" /></td>
                                <td><c:out value="${harv.user}" /></td>
                                <td><fmt:formatDate value="${harv.datetimeStarted}"
                                    pattern="dd-MM-yy HH:mm:ss" /></td>
                                <td><fmt:formatDate value="${harv.datetimeFinished}"
                                    pattern="dd-MM-yy HH:mm:ss" /></td>
                                <td><c:out value="${harv.totalStatements}" /></td>
                                <td><c:out value="${harv.durationString}" /></td>
                                <td><c:out value="${harv.responseCodeString}" /></td>
                                <td><stripes:link href="/harvest.action">
                                    <img src="${pageContext.request.contextPath}/images/view2.gif"
                                        title="View" alt="View" />
                                    <c:if
                                        test="${(!(empty harv.hasFatals) && harv.hasFatals) || (!(empty harv.hasErrors) && harv.hasErrors)}">
                                        <img src="${pageContext.request.contextPath}/images/error.png"
                                            title="Errors" alt="Errors" />
                                    </c:if>
                                    <c:if test="${!(empty harv.hasWarnings) && harv.hasWarnings}">
                                        <img
                                            src="${pageContext.request.contextPath}/images/warning.png"
                                            title="Warnings" alt="Warnings" />
                                    </c:if>
                                    <stripes:param name="harvestDTO.harvestId"
                                        value="${harv.harvestId}" />
                                </stripes:link></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="important-msg">No history found!</div>
            </c:otherwise>
        </c:choose>

    </stripes:layout-component>

</stripes:layout-render>