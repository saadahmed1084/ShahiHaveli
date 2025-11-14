package application.database;

import application.models.MenuItem;

/**
 * Utility class to initialize the database with sample Pakistani/Desi menu items
 * Run this once to populate the menu
 */
public class InitializeMenuData {
    
    public static void populateMenu() {
        // Initialize database tables
        DatabaseConnection.initializeDatabase();
        
        // Sample Pakistani/Desi menu items
        MenuItem[] menuItems = {
            // Biryani Category
            new MenuItem(0, "Chicken Biryani", "Fragrant basmati rice cooked with tender chicken pieces, aromatic spices, and herbs", "Biryani", 850.00, true),
            new MenuItem(0, "Mutton Biryani", "Traditional biryani with succulent mutton pieces, slow-cooked to perfection", "Biryani", 1200.00, true),
            new MenuItem(0, "Beef Biryani", "Rich and flavorful beef biryani with premium spices", "Biryani", 1100.00, true),
            new MenuItem(0, "Vegetable Biryani", "Delicious vegetarian biryani with mixed vegetables and aromatic rice", "Biryani", 650.00, true),
            
            // Karahi Category
            new MenuItem(0, "Chicken Karahi", "Spicy and tangy chicken cooked in a traditional wok with tomatoes and green chilies", "Karahi", 950.00, true),
            new MenuItem(0, "Mutton Karahi", "Tender mutton pieces in a rich, spicy karahi gravy", "Karahi", 1300.00, true),
            new MenuItem(0, "Beef Karahi", "Hearty beef karahi with traditional spices", "Karahi", 1200.00, true),
            new MenuItem(0, "Chicken White Karahi", "Creamy white karahi with mild spices and yogurt", "Karahi", 1000.00, true),
            
            // Naan & Breads
            new MenuItem(0, "Butter Naan", "Soft, fluffy naan brushed with butter", "Breads", 80.00, true),
            new MenuItem(0, "Garlic Naan", "Naan topped with garlic and herbs", "Breads", 100.00, true),
            new MenuItem(0, "Roghni Naan", "Rich naan with ghee and sesame seeds", "Breads", 120.00, true),
            new MenuItem(0, "Tandoori Roti", "Traditional whole wheat roti from the tandoor", "Breads", 60.00, true),
            new MenuItem(0, "Paratha", "Flaky, layered flatbread", "Breads", 90.00, true),
            
            // Curries & Gravies
            new MenuItem(0, "Chicken Korma", "Mild, creamy curry with tender chicken in a rich sauce", "Curries", 900.00, true),
            new MenuItem(0, "Mutton Korma", "Royal mutton curry in a creamy, aromatic gravy", "Curries", 1250.00, true),
            new MenuItem(0, "Chicken Handi", "Chicken cooked in a clay pot with special spices", "Curries", 950.00, true),
            new MenuItem(0, "Daal Makhani", "Creamy black lentils slow-cooked with butter and cream", "Curries", 450.00, true),
            new MenuItem(0, "Chana Masala", "Spiced chickpeas in a tangy tomato gravy", "Curries", 400.00, true),
            
            // BBQ & Grilled
            new MenuItem(0, "Chicken Tikka", "Marinated chicken pieces grilled to perfection", "BBQ", 850.00, true),
            new MenuItem(0, "Seekh Kebab", "Spiced minced meat skewers, grilled on charcoal", "BBQ", 900.00, true),
            new MenuItem(0, "Chapli Kebab", "Traditional Peshawari-style flat kebab", "BBQ", 950.00, true),
            new MenuItem(0, "Reshmi Kebab", "Tender, silky kebabs with cream and cashews", "BBQ", 1000.00, true),
            new MenuItem(0, "Beef Boti", "Marinated beef chunks, grilled with spices", "BBQ", 1100.00, true),
            
            // Rice Dishes
            new MenuItem(0, "Chicken Pulao", "Fragrant rice cooked with chicken and whole spices", "Rice", 750.00, true),
            new MenuItem(0, "Mutton Pulao", "Aromatic rice with tender mutton pieces", "Rice", 1100.00, true),
            new MenuItem(0, "Plain Rice", "Steamed basmati rice", "Rice", 200.00, true),
            
            // Desserts
            new MenuItem(0, "Gulab Jamun", "Sweet milk dumplings in rose-flavored syrup", "Desserts", 150.00, true),
            new MenuItem(0, "Kheer", "Creamy rice pudding with nuts and cardamom", "Desserts", 200.00, true),
            new MenuItem(0, "Zarda", "Sweet saffron rice with nuts and raisins", "Desserts", 250.00, true),
            new MenuItem(0, "Gajar Ka Halwa", "Carrot pudding cooked in milk and ghee", "Desserts", 200.00, true),
            
            // Beverages
            new MenuItem(0, "Lassi", "Traditional yogurt drink, sweet or salty", "Beverages", 150.00, true),
            new MenuItem(0, "Mango Lassi", "Creamy mango-flavored yogurt drink", "Beverages", 200.00, true),
            new MenuItem(0, "Chai", "Traditional Pakistani tea", "Beverages", 100.00, true),
            new MenuItem(0, "Fresh Lime", "Refreshing lime drink", "Beverages", 120.00, true)
        };
        
        // Insert menu items
        int successCount = 0;
        for (MenuItem item : menuItems) {
            if (MenuDAO.addMenuItem(item)) {
                successCount++;
            }
        }
        
        System.out.println("Menu initialization complete! Added " + successCount + " items.");
    }
    
    public static void main(String[] args) {
        populateMenu();
    }
}



