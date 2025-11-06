package com.agnezdei.hotel.ui;

public enum MenuCommand {
    EXIT(0, "Выход"),
    SETTLE_GUEST(1, "Заселить гостя"),
    EVICT_GUEST(2, "Выселить гостя"),
    SET_ROOM_MAINTENANCE(3, "Перевести номер на ремонт"),
    SET_ROOM_AVAILABLE(4, "Сделать номер доступным"),
    CHANGE_ROOM_PRICE(5, "Изменить цену номера"),
    CHANGE_SERVICE_PRICE(6, "Изменить цену услуги"),
    ADD_ROOM(7, "Добавить номер"),
    ADD_SERVICE(8, "Добавить услугу"),
    
    SHOW_ALL_ROOMS_PRICE(9, "Все номера (по цене)"),
    SHOW_ALL_ROOMS_CAPACITY(10, "Все номера (по вместимости)"),
    SHOW_ALL_ROOMS_STARS(11, "Все номера (по звездам)"),
    SHOW_AVAILABLE_ROOMS_PRICE(12, "Свободные номера (по цене)"),
    SHOW_AVAILABLE_ROOMS_CAPACITY(13, "Свободные номера (по вместимости)"),
    SHOW_AVAILABLE_ROOMS_STARS(14, "Свободные номера (по звездам)"),
    
    SHOW_GUESTS_NAME(15, "Постояльцы (по имени)"),
    SHOW_GUESTS_CHECKOUT(16, "Постояльцы (по дате выезда)"),
    SHOW_TOTAL_AVAILABLE(17, "Общее число свободных номеров"),
    SHOW_TOTAL_GUESTS(18, "Общее число постояльцев"),
    SHOW_ROOMS_BY_DATE(19, "Номера свободные на дату"),
    SHOW_PAYMENT_AMOUNT(20, "Сумма оплаты за номер"),
    SHOW_LAST_THREE_GUESTS(21, "3 последних постояльца номера"),
    
    SHOW_GUEST_SERVICES_PRICE(22, "Услуги гостя (по цене)"),
    SHOW_GUEST_SERVICES_DATE(23, "Услуги гостя (по дате)"),
    SHOW_PRICE_LIST(24, "Прайс-лист"),
    SHOW_ROOM_DETAILS(25, "Детали номера"),
    ADD_SERVICE_TO_BOOKING(26, "Добавить услугу к бронированию");

    private final int code;
    private final String description;

    MenuCommand(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MenuCommand fromCode(int code) {
        for (MenuCommand command : values()) {
            if (command.getCode() == code) {
                return command;
            }
        }
        return null;
    }

    public static void printMenu() {
        System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
        
        System.out.println("\n--- УПРАВЛЕНИЕ ---");
        for (MenuCommand command : values()) {
            if (command.getCode() >= 1 && command.getCode() <= 8) {
                System.out.println(command.getCode() + ". " + command.getDescription());
            }
        }
        
        System.out.println("\n--- ОТЧЕТЫ: НОМЕРА ---");
        for (MenuCommand command : values()) {
            if (command.getCode() >= 9 && command.getCode() <= 14) {
                System.out.println(command.getCode() + ". " + command.getDescription());
            }
        }
        
        System.out.println("\n--- ОТЧЕТЫ: ГОСТИ ---");
        for (MenuCommand command : values()) {
            if (command.getCode() >= 15 && command.getCode() <= 21) {
                System.out.println(command.getCode() + ". " + command.getDescription());
            }
        }
        
        System.out.println("\n--- ОТЧЕТЫ: УСЛУГИ ---");
        for (MenuCommand command : values()) {
            if (command.getCode() >= 22 && command.getCode() <= 25) {
                System.out.println(command.getCode() + ". " + command.getDescription());
            }
        }
        
        System.out.println("\n0. Выход");
        System.out.print("Выберите действие: ");
    }
}