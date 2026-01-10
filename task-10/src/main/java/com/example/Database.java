package com.example;

import java.sql.*;

public class Database {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:computer_store.db");
            
            createTables(connection);
            insertTestData(connection);
            executeQueries(connection);
            
            connection.close();
            
        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        stmt.execute("DROP TABLE IF EXISTS Printer");
        stmt.execute("DROP TABLE IF EXISTS Laptop");
        stmt.execute("DROP TABLE IF EXISTS PC");
        stmt.execute("DROP TABLE IF EXISTS Product");

        String productTable = """
            CREATE TABLE Product (
                maker VARCHAR(10) NOT NULL,      -- производитель
                model VARCHAR(50) PRIMARY KEY,   -- номер модели (уникальный)
                type VARCHAR(10) NOT NULL        -- тип: PC, Laptop или Printer
            )
            """;

        String pcTable = """
            CREATE TABLE PC (
                code INTEGER PRIMARY KEY AUTOINCREMENT,
                model VARCHAR(50) NOT NULL,
                speed INTEGER NOT NULL,
                ram INTEGER NOT NULL,
                hd REAL NOT NULL,
                cd VARCHAR(10) NOT NULL,
                price REAL NOT NULL,
                FOREIGN KEY (model) REFERENCES Product(model) ON DELETE CASCADE
            )
            """;
        
        String laptopTable = """
            CREATE TABLE Laptop (
                code INTEGER PRIMARY KEY AUTOINCREMENT,
                model VARCHAR(50) NOT NULL,
                speed INTEGER NOT NULL,
                ram INTEGER NOT NULL,
                hd REAL NOT NULL,
                screen INTEGER NOT NULL,
                price REAL NOT NULL,
                FOREIGN KEY (model) REFERENCES Product(model) ON DELETE CASCADE
            )
            """;
        
        String printerTable = """
            CREATE TABLE Printer (
                code INTEGER PRIMARY KEY AUTOINCREMENT,
                model VARCHAR(50) NOT NULL,
                color CHAR(1) NOT NULL CHECK (color IN ('y', 'n')),
                type VARCHAR(10) NOT NULL CHECK (type IN ('Laser', 'Jet', 'Matrix')),
                price REAL NOT NULL,
                FOREIGN KEY (model) REFERENCES Product(model) ON DELETE CASCADE
            )
            """;
        
        stmt.execute(productTable);
        stmt.execute(pcTable);
        stmt.execute(laptopTable);
        stmt.execute(printerTable);
        
        stmt.close();
    }
    
    private static void insertTestData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String[] products = {
            "INSERT INTO Product VALUES ('A', '1001', 'PC')",
            "INSERT INTO Product VALUES ('A', '1002', 'PC')",
            "INSERT INTO Product VALUES ('A', '1003', 'Laptop')",
            "INSERT INTO Product VALUES ('B', '2001', 'PC')",
            "INSERT INTO Product VALUES ('B', '2002', 'Laptop')",
            "INSERT INTO Product VALUES ('B', '2003', 'Printer')",
            "INSERT INTO Product VALUES ('C', '3001', 'Laptop')",
            "INSERT INTO Product VALUES ('C', '3002', 'Printer')",
            "INSERT INTO Product VALUES ('D', '4001', 'PC')",
            "INSERT INTO Product VALUES ('E', '5001', 'Laptop')",
            "INSERT INTO Product VALUES ('E', '5002', 'Laptop')",
            "INSERT INTO Product VALUES ('F', '6001', 'PC')",
            "INSERT INTO Product VALUES ('F', '6002', 'PC')",
            "INSERT INTO Product VALUES ('F', '6003', 'PC')",
            "INSERT INTO Product VALUES ('F', '6004', 'PC')",
            "INSERT INTO Product VALUES ('G', '7001', 'PC')",
            "INSERT INTO Product VALUES ('G', '7002', 'Printer')",
            "INSERT INTO Product VALUES ('G', '7003', 'PC')",
            "INSERT INTO Product VALUES ('H', '8001', 'PC')",
            "INSERT INTO Product VALUES ('H', '8002', 'Printer')",
            "INSERT INTO Product VALUES ('I', '9001', 'Printer')",
            "INSERT INTO Product VALUES ('J', '9002', 'Laptop')",
            "INSERT INTO Product VALUES ('K', '9003', 'PC')"
        };
        
        String[] pcs = {
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('1001', 450, 512, 80.0, '12x', 450.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('1002', 600, 1024, 120.0, '24x', 550.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('2001', 750, 2048, 240.0, '12x', 800.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('4001', 450, 512, 80.0, '24x', 400.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('6001', 1000, 4096, 500.0, '48x', 1200.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('6002', 1100, 8192, 1000.0, '52x', 1500.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('7001', 500, 256, 60.0, '12x', 350.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('7003', 700, 256, 80.0, '24x', 450.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('8001', 400, 256, 40.0, '12x', 300.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('6003', 800, 2048, 320.0, '24x', 650.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('6004', 900, 4096, 500.0, '48x', 850.00)",
            "INSERT INTO PC (model, speed, ram, hd, cd, price) VALUES ('9003', 1200, 8192, 2000.0, '72x', 2500.00)"
        };
        
        String[] laptops = {
            "INSERT INTO Laptop (model, speed, ram, hd, screen, price) VALUES ('1003', 600, 1024, 100.0, 15, 1200.00)",
            "INSERT INTO Laptop (model, speed, ram, hd, screen, price) VALUES ('2002', 800, 2048, 200.0, 17, 1500.00)",
            "INSERT INTO Laptop (model, speed, ram, hd, screen, price) VALUES ('3001', 900, 4096, 500.0, 13, 2000.00)",
            "INSERT INTO Laptop (model, speed, ram, hd, screen, price) VALUES ('5001', 300, 512, 60.0, 14, 600.00)",
            "INSERT INTO Laptop (model, speed, ram, hd, screen, price) VALUES ('5002', 350, 1024, 120.0, 15, 750.00)",
            "INSERT INTO Laptop (model, speed, ram, hd, screen, price) VALUES ('9002', 1300, 16384, 1000.0, 15, 3000.00)"
        };
        
        String[] printers = {
            "INSERT INTO Printer (model, color, type, price) VALUES ('2003', 'y', 'Laser', 300.00)",
            "INSERT INTO Printer (model, color, type, price) VALUES ('3002', 'n', 'Jet', 150.00)",
            "INSERT INTO Printer (model, color, type, price) VALUES ('7002', 'y', 'Laser', 400.00)",
            "INSERT INTO Printer (model, color, type, price) VALUES ('8002', 'n', 'Jet', 200.00)",
            "INSERT INTO Printer (model, color, type, price) VALUES ('9001', 'y', 'Matrix', 100.00)"
        };
        
        for (String sql : products) stmt.execute(sql);
        for (String sql : pcs) stmt.execute(sql);
        for (String sql : laptops) stmt.execute(sql);
        for (String sql : printers) stmt.execute(sql);
        
        stmt.close();
    }
    

    private static void executeQueries(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        System.out.println("1. ПК дешевле $500:");
        ResultSet rs = stmt.executeQuery("SELECT model, speed, hd FROM PC WHERE price < 500");
        while (rs.next()) {
            System.out.printf("  Модель: %s, Скорость: %d МГц, HDD: %.0f ГБ%n",
                rs.getString("model"), rs.getInt("speed"), rs.getDouble("hd"));
        }
        
        System.out.println("\n2. Производители принтеров:");
        rs = stmt.executeQuery("SELECT DISTINCT maker FROM Product WHERE type = 'Printer'");
        while (rs.next()) {
            System.out.println("  " + rs.getString("maker"));
        }
        
        System.out.println("\n3. Ноутбуки дороже $1000:");
        rs = stmt.executeQuery("SELECT model, ram, screen FROM Laptop WHERE price > 1000");
        while (rs.next()) {
            System.out.printf("  Модель: %s, RAM: %d МБ, Экран: %d дюймов%n",
                rs.getString("model"), rs.getInt("ram"), rs.getInt("screen"));
        }
        
        System.out.println("\n4. Все цветные принтеры:");
        rs = stmt.executeQuery("SELECT * FROM Printer WHERE color = 'y'");
        while (rs.next()) {
            System.out.printf("  Код: %d, Модель: %s, Тип: %s, Цена: $%.2f%n",
                rs.getInt("code"), rs.getString("model"),
                rs.getString("type"), rs.getDouble("price"));
        }

        System.out.println("\n5. ПК с CD 12x/24x и цена < $600:");
        rs = stmt.executeQuery("SELECT model, speed, hd FROM PC WHERE cd IN ('12x', '24x') AND price < 600");
        while (rs.next()) {
            System.out.printf("  Модель: %s, Скорость: %d МГц, HDD: %.0f ГБ%n",
                rs.getString("model"), rs.getInt("speed"), rs.getDouble("hd"));
        }

        System.out.println("\n6. Производители ноутбуков с HDD >= 100 ГБ:");
        String query6 = """
            SELECT Product.maker, Laptop.speed 
            FROM Laptop 
            JOIN Product ON Laptop.model = Product.model 
            WHERE Laptop.hd >= 100
            """;
        rs = stmt.executeQuery(query6);
        while (rs.next()) {
            System.out.printf("  Производитель: %s, Скорость: %d МГц%n",
                rs.getString("maker"), rs.getInt("speed"));
        }
        
        System.out.println("\n7. Все продукты производителя B:");
        rs = stmt.executeQuery("""
            SELECT p.model, pc.price 
            FROM Product p 
            JOIN PC pc ON p.model = pc.model 
            WHERE p.maker = 'B'
            UNION
            SELECT p.model, l.price 
            FROM Product p 
            JOIN Laptop l ON p.model = l.model 
            WHERE p.maker = 'B'
            UNION
            SELECT p.model, pr.price 
            FROM Product p 
            JOIN Printer pr ON p.model = pr.model 
            WHERE p.maker = 'B'
            """);
        while (rs.next()) {
            System.out.printf("  Модель: %s, Цена: $%.2f%n",
                rs.getString("model"), rs.getDouble("price"));
        }
        
        System.out.println("\n8. Производители, которые делают ПК, но не ноутбуки:");
        rs = stmt.executeQuery("""
            SELECT DISTINCT maker 
            FROM Product 
            WHERE type = 'PC' 
            AND maker NOT IN (
                SELECT DISTINCT maker 
                FROM Product 
                WHERE type = 'Laptop'
            )
            """);
        while (rs.next()) {
            System.out.println("  " + rs.getString("maker"));
        }

        System.out.println("\n9. Производители ПК с процессором >= 450 МГц:");
        rs = stmt.executeQuery("""
            SELECT DISTINCT p.maker 
            FROM Product p 
            JOIN PC pc ON p.model = pc.model 
            WHERE pc.speed >= 450
            """);
        while (rs.next()) {
            System.out.println("  " + rs.getString("maker"));
        }
        
        System.out.println("\n10. Самые дорогие принтеры:");
        rs = stmt.executeQuery("""
            SELECT model, price 
            FROM Printer 
            WHERE price = (SELECT MAX(price) FROM Printer)
            """);
        while (rs.next()) {
            System.out.printf("  Модель: %s, Цена: $%.2f%n",
                rs.getString("model"), rs.getDouble("price"));
        }
        
        System.out.println("\n11. Средняя скорость ПК:");
        rs = stmt.executeQuery("SELECT AVG(speed) as avg_speed FROM PC");
        while (rs.next()) {
            System.out.printf("  Средняя скорость: %.2f МГц%n", rs.getDouble("avg_speed"));
        }
        
        System.out.println("\n12. Средняя скорость ноутбуков дороже $1000:");
        rs = stmt.executeQuery("SELECT AVG(speed) as avg_speed FROM Laptop WHERE price > 1000");
        while (rs.next()) {
            System.out.printf("  Средняя скорость: %.2f МГц%n", rs.getDouble("avg_speed"));
        }
        
        System.out.println("\n13. Средняя скорость ПК производителя A:");
        rs = stmt.executeQuery("""
            SELECT AVG(pc.speed) as avg_speed
            FROM PC pc 
            JOIN Product p ON pc.model = p.model 
            WHERE p.maker = 'A'
            """);
        while (rs.next()) {
            System.out.printf("  Средняя скорость: %.2f МГц%n", rs.getDouble("avg_speed"));
        }
        
        System.out.println("\n14. Средняя цена ПК по скоростям:");
        rs = stmt.executeQuery("SELECT speed, AVG(price) as avg_price FROM PC GROUP BY speed");
        while (rs.next()) {
            System.out.printf("  Скорость: %d МГц, Средняя цена: $%.2f%n",
                rs.getInt("speed"), rs.getDouble("avg_price"));
        }

        System.out.println("\n15. Размеры HDD, которые есть у 2+ ПК:");
        rs = stmt.executeQuery("SELECT hd FROM PC GROUP BY hd HAVING COUNT(*) >= 2");
        while (rs.next()) {
            System.out.printf("  HDD: %.0f ГБ%n", rs.getDouble("hd"));
        }
        
        System.out.println("\n16. Пары ПК с одинаковой скоростью и RAM:");
        rs = stmt.executeQuery("""
            SELECT DISTINCT pc1.model as model1, pc2.model as model2, pc1.speed, pc1.ram
            FROM PC pc1, PC pc2
            WHERE pc1.speed = pc2.speed 
              AND pc1.ram = pc2.ram
              AND pc1.model < pc2.model
            ORDER BY model1
            """);
        boolean hasPairs = false;
        while (rs.next()) {
            hasPairs = true;
            System.out.printf("  Пара: %s и %s, Скорость: %d МГц, RAM: %d МБ%n",
                rs.getString("model1"), rs.getString("model2"),
                rs.getInt("speed"), rs.getInt("ram"));
        }
        if (!hasPairs) {
            System.out.println("  (Нет пар с одинаковыми характеристиками)");
        }
        
        System.out.println("\n17. Ноутбуки, медленнее любого ПК:");
        rs = stmt.executeQuery("""
            SELECT p.type, l.model, l.speed
            FROM Laptop l
            JOIN Product p ON l.model = p.model
            WHERE l.speed < (
              SELECT MIN(speed)
              FROM PC
            )
            """);
        boolean hasSlowerLaptops = false;
        while (rs.next()) {
            hasSlowerLaptops = true;
            System.out.printf("  Тип: %s, Модель: %s, Скорость: %d МГц%n",
                rs.getString("type"), rs.getString("model"), rs.getInt("speed"));
        }
        if (!hasSlowerLaptops) {
            System.out.println("  (Все ноутбуки быстрее или равны самому медленному ПК)");
        }
        
        System.out.println("\n18. Производители самых дешевых цветных принтеров:");
        rs = stmt.executeQuery("""
            SELECT p.maker, pr.price
            FROM Printer pr
            JOIN Product p ON pr.model = p.model
            WHERE pr.color = 'y'
              AND pr.price = (SELECT MIN(price) FROM Printer WHERE color = 'y')
            """);
        while (rs.next()) {
            System.out.printf("  Производитель: %s, Цена: $%.2f%n",
                rs.getString("maker"), rs.getDouble("price"));
        }
        
        System.out.println("\n19. Средний размер экрана ноутбуков по производителям:");
        rs = stmt.executeQuery("""
            SELECT p.maker, AVG(l.screen) as avg_screen
            FROM Laptop l
            JOIN Product p ON l.model = p.model
            GROUP BY p.maker
            """);
        while (rs.next()) {
            System.out.printf("  Производитель: %s, Средний экран: %.1f дюймов%n",
                rs.getString("maker"), rs.getDouble("avg_screen"));
        }
        
        System.out.println("\n20. Производители с 3+ моделями ПК:");
        rs = stmt.executeQuery("""
            SELECT p.maker, COUNT(*) as model_count
            FROM Product p
            WHERE p.type = 'PC'
            GROUP BY p.maker
            HAVING COUNT(*) >= 3
            """);
        boolean hasMakers = false;
        while (rs.next()) {
            hasMakers = true;
            System.out.printf("  Производитель: %s, Количество моделей: %d%n",
                rs.getString("maker"), rs.getInt("model_count"));
        }
        if (!hasMakers) {
            System.out.println("  (Нет производителей с 3+ моделями ПК)");
        }
        
        System.out.println("\n21. Максимальная цена ПК по производителям:");
        rs = stmt.executeQuery("""
            SELECT p.maker, MAX(pc.price) as max_price
            FROM PC pc
            JOIN Product p ON pc.model = p.model
            GROUP BY p.maker
            """);
        while (rs.next()) {
            System.out.printf("  Производитель: %s, Макс. цена: $%.2f%n",
                rs.getString("maker"), rs.getDouble("max_price"));
        }
        
        System.out.println("\n22. Средняя цена ПК со скоростью > 600 МГц:");
        rs = stmt.executeQuery("""
            SELECT speed, AVG(price) as avg_price
            FROM PC
            WHERE speed > 600
            GROUP BY speed
            """);
        boolean hasFastPCs = false;
        while (rs.next()) {
            hasFastPCs = true;
            System.out.printf("  Скорость: %d МГц, Средняя цена: $%.2f%n",
                rs.getInt("speed"), rs.getDouble("avg_price"));
        }
        if (!hasFastPCs) {
            System.out.println("  (Нет ПК со скоростью > 600 МГц)");
        }
        
        System.out.println("\n23. Производители, которые делают и ПК, и ноутбуки со скоростью >= 750 МГц:");
        rs = stmt.executeQuery("""
            SELECT DISTINCT p.maker
            FROM Product p
            JOIN PC pc ON p.model = pc.model
            WHERE pc.speed >= 750
            INTERSECT
            SELECT DISTINCT p.maker
            FROM Product p
            JOIN Laptop l ON p.model = l.model
            WHERE l.speed >= 750
            """);
        boolean hasBoth = false;
        while (rs.next()) {
            hasBoth = true;
            System.out.println("  " + rs.getString("maker"));
        }
        if (!hasBoth) {
            System.out.println("  (Нет таких производителей)");
        }
        
        System.out.println("\n24. Самые дорогие модели всех типов:");
        rs = stmt.executeQuery("""
            SELECT model, price FROM PC WHERE price = (SELECT MAX(price) FROM PC)
            UNION
            SELECT model, price FROM Laptop WHERE price = (SELECT MAX(price) FROM Laptop)
            UNION
            SELECT model, price FROM Printer WHERE price = (SELECT MAX(price) FROM Printer)
            """);
        while (rs.next()) {
            System.out.printf("  Модель: %s, Цена: $%.2f%n",
                rs.getString("model"), rs.getDouble("price"));
        }
        
        System.out.println("\n25. Производители принтеров, которые делают ПК с min RAM и max speed среди min RAM:");
        rs = stmt.executeQuery("""
            SELECT DISTINCT p.maker
            FROM Product p
            JOIN PC pc ON p.model = pc.model
            WHERE pc.ram = (SELECT MIN(ram) FROM PC)
              AND pc.speed = (
                  SELECT MAX(speed) 
                  FROM PC 
                  WHERE ram = (SELECT MIN(ram) FROM PC)
              )
              AND p.maker IN (SELECT maker FROM Product WHERE type = 'Printer')
            """);
        boolean hasPrinterMakers = false;
        while (rs.next()) {
            hasPrinterMakers = true;
            System.out.println("  " + rs.getString("maker"));
        }
        if (!hasPrinterMakers) {
            System.out.println("  (Нет таких производителей)");
        }

        rs.close();
        stmt.close();
    }
}