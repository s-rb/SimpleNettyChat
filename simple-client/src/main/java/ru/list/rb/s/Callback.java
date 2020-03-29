package ru.list.rb.s;

public interface Callback {

    void callback(Object... args); // Метод с любым кол-вом аргументов, для исключения жесткой привязки контроллера к Network
}
