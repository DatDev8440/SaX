package com.sax.utils;

import com.jgoodies.common.collect.ArrayListModel;
import com.sax.views.nhanvien.cart.CartModel;

import javax.swing.*;
import java.util.List;

public class Cart {
    private static List<CartModel> list;

    static {
        list = new ArrayListModel<>();
    }

    public static List<CartModel> getCart() {
        return list;
    }
}
