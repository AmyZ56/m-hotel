package com.hotel.model;

public class Room {
    private int roomId;
    private String roomNumber;
    private String roomType;
    private double price;
    private boolean availability;
    private boolean bookedByUser;
    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Room() {}

    public Room(String roomNumber, String roomType, double price, boolean availability) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.availability = availability;
    }


    // Getter 和 Setter 方法
    public int getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getPrice() { return price; }
    public boolean isAvailability() { return availability; }
    public boolean isBookedByUser() { return bookedByUser; } // 添加 getter 方法

    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setPrice(double price) { this.price = price; }
    public void setAvailability(boolean availability) { this.availability = availability; }
    public void setBookedByUser(boolean bookedByUser) { this.bookedByUser = bookedByUser; } // 添加 setter 方法


}