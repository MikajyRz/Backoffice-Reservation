package com.interfaces;

public interface SessionUserProvider {
    String[] roles = new String[] {};
    boolean getAuth();
    void setAuth(boolean auth);
    String[] getRole();
    void setRole(String[] role);
}
    
