package application.models;

import java.sql.Timestamp;

public class Review {
    private int reviewId;
    private int orderId;
    private Integer userId;
    private String customerName;
    private String contactPhone;
    private String contactEmail;
    private int ambianceRating;
    private int foodRating;
    private int serviceRating;
    private double overallRating;
    private String writtenFeedback;
    private Timestamp createdAt;

    public Review() {}

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public int getAmbianceRating() { return ambianceRating; }
    public void setAmbianceRating(int ambianceRating) { this.ambianceRating = ambianceRating; }

    public int getFoodRating() { return foodRating; }
    public void setFoodRating(int foodRating) { this.foodRating = foodRating; }

    public int getServiceRating() { return serviceRating; }
    public void setServiceRating(int serviceRating) { this.serviceRating = serviceRating; }

    public double getOverallRating() { return overallRating; }
    public void setOverallRating(double overallRating) { this.overallRating = overallRating; }

    public String getWrittenFeedback() { return writtenFeedback; }
    public void setWrittenFeedback(String writtenFeedback) { this.writtenFeedback = writtenFeedback; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}



