# 🎨 **ArtSea – Online Art Auction System**

**ArtSea** is a full-stack, web-based online art auction platform that connects **artists** and **buyers** through a **secure, transparent, and user-friendly digital marketplace**. It allows artists to showcase their artworks and buyers to participate in **real-time auctions** from anywhere in the world.

## 📌 **Project Overview**

Traditional art auctions are limited by **location, time, and high operational costs**. ArtSea overcomes these challenges by providing an online platform where:

* **Artists** can upload and auction their artworks
* **Buyers** can browse, bid, and purchase art in real time
* **Administrators** can manage users, auctions, and transactions

The system is built using **Spring Boot, Hibernate, Thymeleaf, and MySQL** to ensure **efficiency, scalability, and data security**.

## 🎯 **Objectives**

* Build a **responsive and user-friendly** art auction platform
* Enable **secure authentication** with **role-based access**
* Implement a **real-time bidding system**
* Automate **auction scheduling, closure, and winner declaration**
* Ensure **data integrity and transparent auction processes**

## 🧩 **Key Features**

### 👤 **User Management**

* Registration and login for **Artists, Buyers, and Admins**
* **Spring Security** based authentication
* Session management and **email/OTP verification**

### 🖼️ **Artwork & Auction Management**

* Artwork upload and management by artists
* Auction creation with **base price and time scheduling**
* **Automatic auction closure** and winner selection

### 💰 **Bidding System**

* **Real-time bidding** on artworks
* Live auction status updates
* Transparent bid history

### 📊 **Dashboards**

* **Artist dashboard** for artworks and auctions
* **Buyer dashboard** for bids and purchases
* **Admin dashboard** for full system control

## 🛠️ **Technologies Used**

**Frontend:** HTML5, CSS3, JavaScript, Bootstrap
**Backend:** Java, Spring Boot
**Template Engine:** Thymeleaf
**ORM:** Hibernate (JPA)
**Database:** MySQL
**Security:** Spring Security
**Server:** Embedded Apache Tomcat
**Tools:** IntelliJ IDEA / VS Code

## 🚫 **Out of Scope**

* Offline auction handling
* External payment gateway integration (simulated transactions only)
* AI-based artwork recommendations (future enhancement)

## 🚀 **Future Enhancements**

* Payment gateway integration
* AI-based artwork recommendations
* Live notifications and chat system
* Advanced analytics for sellers

## ▶️ **How to Run**

```bash
git clone https://github.com/your-username/artsea.git
```

Configure MySQL → update `application.properties` → run Spring Boot → open
`http://localhost:8080`
