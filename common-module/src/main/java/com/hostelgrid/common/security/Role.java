package com.hostelgrid.common.security;

/**
 * Enum representing user roles in the hostel management system.
 */
public enum Role {
    // Management Hierarchy
    ADMIN,          // Super admin - manages entire system
    OWNER,          // Hostel owner - manages their hostels
    WARDEN,         // Hostel warden - manages hostel operations
    
    // Front Office & Administration
    RECEPTIONIST,   // Front desk - handles check-ins, visitors
    ACCOUNTANT,     // Accountant - handles billing/payments
    
    // Food & Mess Operations
    MESS_MANAGER,   // Mess manager - oversees food operations
    COOK,           // Cook - food preparation
    
    // Maintenance & Operations
    MAINTENANCE_HEAD, // Maintenance supervisor
    MAINTENANCE,    // Generic maintenance staff
    CLEANER,        // Housekeeping & cleaning tasks
    
    // Security & Safety
    SECURITY_HEAD,  // Security supervisor
    SECURITY_GUARD, // Security guard - entry/exit, patrol
    
    // Healthcare & Wellness
    NURSE,          // Medical assistance for students
    
    // Students & Residents
    STUDENT,        // Regular student - books rooms, uses services
    USER,           // Alias for STUDENT (backward compatibility)
    STUDENT_LEADER, // Student representative/coordinator
    
    // External & Temporary
    VISITOR,        // Temporary visitor access
    VENDOR,         // Service providers, suppliers
    PARENT_GUARDIAN // Student's parent/guardian access
}