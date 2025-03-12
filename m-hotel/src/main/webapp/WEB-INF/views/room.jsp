<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hotel.model.Room" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hotel.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
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
    <title>房间管理</title>
    <link rel="stylesheet" href="../../static/css/main.css">
    <link rel="stylesheet" href="../../static/css/room.css">
</head>
<body>

<div class="sidebar">
    <h2>菜单</h2>
    <%
        if ("admin".equals(user.getRole())) { %>
    <button onclick="location.href='${pageContext.request.contextPath}/index'">首页</button>
    <button onclick="location.href='${pageContext.request.contextPath}/rooms'">房间管理</button>
    <button onclick="location.href='${pageContext.request.contextPath}/orders'">订单管理</button>
    <%} else if ("user".equals(user.getRole())) { %>
    <button onclick="location.href='${pageContext.request.contextPath}/index'">首页</button>
    <button onclick="location.href='${pageContext.request.contextPath}/rooms'">房间预订</button>
    <button onclick="location.href='${pageContext.request.contextPath}/orders'">查看订单</button>
    <button onclick="location.href='${pageContext.request.contextPath}/update'">修改信息</button>
    <%}%>
    <div class="logout-link">
        <a href="#" onclick="confirmLogout()">登出</a>
    </div>
</div>

<div class="container">
    <% if ("admin".equals(user.getRole())) { %>
    <h1>房间管理</h1>
    <button class="add-room-btn" id="addRoomBtn">添加房间</button>
    <% } %>

    <% if ("user".equals(user.getRole())) { %>
    <h1>预定房间</h1>
    <% } %>

    <% if (successMessage != null) { %>
    <p style="color: green;"><%= successMessage %></p>
    <% } %>
    <% if (errorMessage != null) { %>
    <p style="color: red;"><%= errorMessage %></p>
    <% } %>

    <form action="rooms" method="post" class="search-form">
        <input type="hidden" name="action" value="search">
        <input type="text" name="keyword" placeholder="搜索房间号或类型" class="search-input">
        <button type="submit" class="search-btn">搜索</button>
    </form>

    <% if (rooms == null || rooms.isEmpty()) { %>
    <p style="color: gray;">没有房间数据。</p>
    <% } else { %>
    <table>
        <thead>
        <tr>
            <th>房间号</th>
            <th>房间类型</th>
            <th>价格</th>
            <% if ("admin".equals(user.getRole())) { %>
            <th>是否可用</th>
            <th>操作</th>
            <% } else { %>
            <th>状态</th>
            <th>操作</th>
            <% } %>
        </tr>
        </thead>
        <tbody>
        <% for (Room room : rooms) { %>
        <tr>
            <td><%= room.getRoomNumber() %></td>
            <td><%= room.getRoomType() %></td>
            <td><%= room.getPrice() %></td>
            <% if ("admin".equals(user.getRole())) { %>
            <td>
                <% if (room.isAvailability()) { %>
                <span style="color: green;">可用</span>
                <% } else { %>
                <span style="color: red;">不可用</span>
                <% } %>
            </td>
            <td>
                <button class="edit-btn" onclick="openEditModal(<%= room.getRoomId() %>, '<%= room.getRoomNumber() %>', '<%= room.getRoomType() %>', <%= room.getPrice() %>, <%= room.isAvailability() %>)">编辑</button>
                <button class="delete-btn" onclick="openDeleteModal(<%= room.getRoomId() %>)">删除</button>
            </td>
            <% } else { %>
            <td>
                <% if (room.isAvailability()) { %>
                <span style="color: green;">可用</span>
                <% } else { %>
                <span style="color: red;">已预订</span>
                <% } %>
            </td>
            <td>
                <% if (room.isAvailability()) { %>
                <button class="reserve-btn" onclick="openReservationModal(<%= room.getRoomId() %>)">预订</button>
                <% } else { %>
                <button class="view-orders-btn" onclick="location.href='${pageContext.request.contextPath}/orders'">查看订单</button>
                <% } %>
            </td>
            <% } %>
            <!-- 显示房间图片 -->
            <td>
                <% if (room.getImagePath() != null && !room.getImagePath().isEmpty()) { %>
                <img src="<%= request.getContextPath() + room.getImagePath() %>" alt="Room Image" style="max-width: 100px; max-height: 100px;">
                <% } else { %>
                <span>无图片</span>
                <% } %>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
    <% }
        Room room;%>
</div>

<!-- 添加房间模态框 -->
<div id="addRoomModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>添加房间</h2>
        <form action="rooms" method="post" enctype="multipart/form-data"> <!-- 使用 multipart/form-data -->
            <input type="hidden" name="action" value="add">
            <div class="form-group">
                <label>房间号：</label>
                <input type="text" name="roomNumber" required>
            </div>
            <div class="form-group">
                <label>房间类型：</label>
                <select name="roomType" required>
                    <option value="大床房">大床房</option>
                    <option value="标准房">标准房</option>
                    <option value="亲子房">亲子房</option>
                </select>
            </div>
            <div class="form-group">
                <label>价格：</label>
                <input type="number" name="price" required>
            </div>
            <div class="form-group">
                <label>是否可用：</label>
                <input type="checkbox" name="availability" value="true">
            </div>
            <div class="form-group">
                <label>房间图片：</label>
                <input type="file" name="roomImage" accept="image/*" required>
            </div>
            <button type="submit">确认添加</button>
        </form>
    </div>
</div>

<!-- 编辑房间模态框 -->
<div id="editRoomModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>编辑房间</h2>
        <form action="rooms" method="post" enctype="multipart/form-data"> <!-- 使用 multipart/form-data -->
            <input type="hidden" name="action" value="update">
            <input type="hidden" id="editRoomId" name="roomId">
            <div class="form-group">
                <label>房间号：</label>
                <input type="text" id="editRoomNumber" name="roomNumber" required>
            </div>
            <div class="form-group">
                <label>房间类型：</label>
                <select id="editRoomType" name="roomType" required>
                    <option value="大床房">大床房</option>
                    <option value="标准房">标准房</option>
                    <option value="亲子房">亲子房</option>
                </select>
            </div>
            <div class="form-group">
                <label>价格：</label>
                <input type="number" id="editPrice" name="price" required>
            </div>
            <div class="form-group">
                <label>是否可用：</label>
                <input type="checkbox" id="editAvailability" name="availability">
            </div>
            <div class="form-group">
                <label>房间图片：</label>
                <input type="file" name="roomImage" accept="image/*"> <!-- 可选 -->
            </div>
            <button type="submit">保存</button>
        </form>
    </div>
</div>

<!-- 删除确认模态框 -->
<div id="deleteRoomModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>确认删除</h2>
        <p>确定要删除此房间吗？</p>
        <form action="rooms" method="post">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" id="deleteRoomId" name="roomId">
            <button type="submit">确定</button>
            <button type="button" onclick="closeDeleteModal()">取消</button>
        </form>
    </div>
</div>

<!-- 预订房间模态框 -->
<div id="reservationModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>预订房间</h2>
        <form action="rooms" method="post">
            <input type="hidden" name="action" value="book">
            <input type="hidden" id="bookRoomId" name="roomId">
            <div class="form-group">
                <label>开始日期：</label>
                <input type="date" name="startDate" required>
            </div>
            <div class="form-group">
                <label>结束日期：</label>
                <input type="date" name="endDate" required>
            </div>
            <button type="submit">确认预订</button>
        </form>
    </div>
</div>

<script>
    function confirmLogout() {
        if (confirm("确定要登出吗？")) {
            window.location.href = "../../login.jsp";
        }
    }

    // 显示添加房间模态框
    document.getElementById("addRoomBtn").onclick = function() {
        document.getElementById("addRoomModal").style.display = "block";
    };

    // 显示编辑房间模态框
    function openEditModal(roomId, roomNumber, roomType, price, availability) {
        document.getElementById("editRoomId").value = roomId;
        document.getElementById("editRoomNumber").value = roomNumber;
        // 设置房间类型选中值
        var editRoomType = document.getElementById("editRoomType");
        if (roomType === "大床房") {
            editRoomType.value = "大床房";
        } else if (roomType === "标准房") {
            editRoomType.value = "标准房";
        } else if (roomType === "亲子房") {
            editRoomType.value = "亲子房";
        }
        document.getElementById("editPrice").value = price;
        document.getElementById("editAvailability").checked = availability;
        document.getElementById("editRoomModal").style.display = "block";
    }

    // 显示删除确认模态框
    function openDeleteModal(roomId) {
        document.getElementById("deleteRoomId").value = roomId;
        document.getElementById("deleteRoomModal").style.display = "block";
    }

    // 显示预订房间模态框
    function openReservationModal(roomId) {
        document.getElementById("bookRoomId").value = roomId;
        document.getElementById("reservationModal").style.display = "block";
    }

    // 关闭模态框
    function closeDeleteModal() {
        document.getElementById("deleteRoomModal").style.display = "none";
    }

    // 关闭模态框的通用逻辑
    var closeBtns = document.querySelectorAll(".close");
    closeBtns.forEach(function (btn) {
        btn.onclick = function () {
            document.getElementById("addRoomModal").style.display = "none";
            document.getElementById("editRoomModal").style.display = "none";
            document.getElementById("deleteRoomModal").style.display = "none";
            document.getElementById("reservationModal").style.display = "none";
        };
    });

    // 点击模态框外部关闭
    window.onclick = function (event) {
        var modals = [document.getElementById("addRoomModal"), document.getElementById("editRoomModal"), document.getElementById("deleteRoomModal"), document.getElementById("reservationModal")];
        modals.forEach(function (modal) {
            if (event.target === modal) {
                modal.style.display = "none";
            }
        });
    };
</script>
</body>
</html>