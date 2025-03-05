<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hotel.model.Order" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hotel.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");

    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>订单管理</title>
    <link rel="stylesheet" href="../../static/css/main.css">
    <link rel="stylesheet" href="../../static/css/order.css">
</head>
<body>

<div class="sidebar">
    <h2>菜单</h2>
    <%
        if (user != null) {
            if ("admin".equals(user.getRole())) {
    %>
    <button onclick="location.href='${pageContext.request.contextPath}/index'">首页</button>
    <button onclick="location.href='${pageContext.request.contextPath}/rooms'">房间管理</button>
    <button onclick="location.href='${pageContext.request.contextPath}/orders'">订单管理</button>
    <%
    } else if ("user".equals(user.getRole())) {
    %>
    <button onclick="location.href='${pageContext.request.contextPath}/index'">首页</button>
    <button onclick="location.href='${pageContext.request.contextPath}/rooms'">房间预定</button>
    <button onclick="location.href='${pageContext.request.contextPath}/orders'">查看订单</button>
    <button onclick="location.href='${pageContext.request.contextPath}/update'">修改信息</button>
    <%
            }
        }
    %>
    <div class="logout-link">
        <a href="#" onclick="confirmLogout()">登出</a>
    </div>
</div>

<div class="container">
    <% if ("admin".equals(user.getRole())) { %>
    <h1>订单管理</h1>
    <button class="add-order-btn" id="addOrderBtn">添加订单</button>
    <% } %>

    <% if ("user".equals(user.getRole())) { %>
    <h1>查看订单</h1>
    <% } %>

    <% if (successMessage != null) { %>
    <p style="color: green;"><%= successMessage %></p>
    <% } %>
    <% if (errorMessage != null) { %>
    <p style="color: red;"><%= errorMessage %></p>
    <% } %>

    <% if (orders == null || orders.isEmpty()) { %>
    <p style="color: gray;">没有订单数据。</p>
    <% } else { %>
    <form action="orders" method="get" class="search-form">
        <input type="hidden" name="action" value="search">
        <input type="text" name="keyword" placeholder="搜索订单号或状态" class="search-input">
        <button type="submit" class="search-btn">搜索</button>
    </form>

    <table>
        <thead>
        <tr>
            <th>订单号</th>
            <% if ("admin".equals(user.getRole())) { %>
            <th>用户姓名</th>
            <th>联系电话</th>
            <% } %>
            <th>房间ID</th>
            <th>开始日期</th>
            <th>结束日期</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <% for (Order order : orders) { %>
        <tr>
            <td><%= order.getOrderId() %></td>
            <% if ("admin".equals(user.getRole())) { %>
            <td><%= order.getUser() != null ? order.getUser().getName() : "N/A" %></td>
            <td><%= order.getUser() != null ? order.getUser().getPhone() : "N/A" %></td>
            <% } %>
            <td><%= order.getRoomId() %></td>
            <td><%= order.getStartDate() %></td>
            <td><%= order.getEndDate() %></td>
            <td><%= order.getStatus() %></td>
            <td>
                <% if ("admin".equals(user.getRole())) { %>
                <button class="editBtn" onclick="showEditModal(<%= order.getOrderId() %>, '<%= order.getUserId() %>', '<%= order.getRoomId() %>', '<%= order.getStartDate() %>', '<%= order.getEndDate() %>', '<%= order.getStatus() %>')">编辑</button>
                <button class="deleteBtn" onclick="showConfirmDelete(<%= order.getOrderId() %>)">删除</button>
                <% } %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% } %>
</div>

<!-- 添加订单模态框 -->
<div id="addOrderModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>添加订单</h2>
        <form action="orders" method="post">
            <input type="hidden" name="action" value="add">
            <div class="form-group">
                <label>用户ID：</label>
                <input type="text" name="userId" required>
            </div>
            <div class="form-group">
                <label>房间ID：</label>
                <input type="text" name="roomId" required>
            </div>
            <div class="form-group">
                <label>开始日期：</label>
                <input type="date" name="startDate" required>
            </div>
            <div class="form-group">
                <label>结束日期：</label>
                <input type="date" name="endDate" required>
            </div>
            <div class="form-group">
                <label>状态：</label>
                <input type="text" name="status" required>
            </div>
            <button type="submit">提交</button>
        </form>
    </div>
</div>

<!-- 编辑订单模态框 -->
<div id="editOrderModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>编辑订单</h2>
        <form id="editForm" action="orders" method="post">
            <input type="hidden" name="action" value="update">
            <input type="hidden" id="editOrderId" name="orderId">
            <div class="form-group">
                <label>用户ID：</label>
                <input type="text" id="editUserId" name="userId" required>
            </div>
            <div class="form-group">
                <label>房间ID：</label>
                <input type="text" id="editRoomId" name="roomId" required>
            </div>
            <div class="form-group">
                <label>开始日期：</label>
                <input type="date" id="editStartDate" name="startDate" required>
            </div>
            <div class="form-group">
                <label>结束日期：</label>
                <input type="date" id="editEndDate" name="endDate" required>
            </div>
            <div class="form-group">
                <label>状态：</label>
                <input type="text" id="editStatus" name="status" required>
            </div>
            <button type="submit">保存</button>
        </form>
    </div>
</div>

<!-- 删除确认模态框 -->
<div id="confirmDeleteModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>确认删除</h2>
        <p>您确定要删除此订单吗？</p>
        <form id="deleteForm" action="orders" method="post">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" id="deleteOrderId" name="orderId">
            <button type="submit">确定</button>
            <button type="button" onclick="closeConfirmDelete()">取消</button>
        </form>
    </div>
</div>

<script>
    function confirmLogout() {
        // 弹出确认框
        if (confirm("确定要登出吗？")) {
            // 如果点击"确定"，跳转到登录页面
            window.location.href = "../../login.jsp";
        } else {
            // 如果点击"取消"，留在当前页面
            return false;
        }
    }
    // 显示添加订单模态框
    document.getElementById("addOrderBtn").onclick = function() {
        document.getElementById("addOrderModal").style.display = "block";
    };

    // 显示编辑订单模态框
    function showEditModal(orderId, userId, roomId, startDate, endDate, status) {
        document.getElementById("editOrderId").value = orderId;
        document.getElementById("editUserId").value = userId;
        document.getElementById("editRoomId").value = roomId;
        document.getElementById("editStartDate").value = startDate.split(" ")[0]; // 处理日期格式
        document.getElementById("editEndDate").value = endDate.split(" ")[0];
        document.getElementById("editStatus").value = status;
        document.getElementById("editOrderModal").style.display = "block";
    }

    // 显示删除确认模态框
    function showConfirmDelete(orderId) {
        document.getElementById("deleteOrderId").value = orderId;
        document.getElementById("confirmDeleteModal").style.display = "block";
    }

    // 关闭模态框
    function closeConfirmDelete() {
        document.getElementById("confirmDeleteModal").style.display = "none";
    }

    // 关闭模态框的通用逻辑
    var closeBtns = document.querySelectorAll(".close");
    closeBtns.forEach(function(btn) {
        btn.onclick = function() {
            document.getElementById("addOrderModal").style.display = "none";
            document.getElementById("editOrderModal").style.display = "none";
            document.getElementById("confirmDeleteModal").style.display = "none";
        };
    });

    // 点击模态框外部关闭
    window.onclick = function(event) {
        if (event.target === document.getElementById("addOrderModal") ||
            event.target === document.getElementById("editOrderModal") ||
            event.target === document.getElementById("confirmDeleteModal")) {
            document.getElementById("addOrderModal").style.display = "none";
            document.getElementById("editOrderModal").style.display = "none";
            document.getElementById("confirmDeleteModal").style.display = "none";
        }
    };
</script>
</body>
</html>