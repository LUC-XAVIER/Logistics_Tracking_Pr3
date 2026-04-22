import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SidebarComponent } from '../../../shared/sidebar/sidebar'; 

interface OrderStep {
  date: string;
  label: string;
  time: string;
  done: boolean;
}

interface Order {
  id: string;
  status: string;
  statusColor: string;
  statusBg: string;
  active: boolean;
  steps: OrderStep[];
  progress: number;
}

interface StatItem {
  icon: SafeHtml;
  label: string;
  value: string;
}

interface VehicleInfo {
  label: string;
  value: string;
}

interface OrderDetail {
  label: string;
  value: string;
}

interface DriverInfo {
  label: string;
  value: string;
}

interface CustomerInfo {
  label: string;
  value: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
  imports: [CommonModule, FormsModule, SidebarComponent]
})
export class AdminDashboardComponent implements OnInit {
  orders: Order[] = [
    {
      id: "#AD345Jk758",
      status: "In Transit",
      statusColor: "#2563EB",
      statusBg: "#EFF6FF",
      active: true,
      steps: [
        { date: "21 Jan", label: "Checking", time: "10:23 AM", done: true },
        { date: "25 Jan", label: "In transit", time: "12:02 PM", done: true },
        { date: "25 Jan", label: "Delivered", time: "--:--", done: false },
      ],
      progress: 66,
    },
    {
      id: "#FR156KL89K",
      status: "Checking",
      statusColor: "#D97706",
      statusBg: "#FFFBEB",
      active: false,
      steps: [
        { date: "22 Jan", label: "Checking", time: "11:28 AM", done: true },
        { date: "26 Jan", label: "In transit", time: "--:--", done: false },
        { date: "30 Jan", label: "Delivered", time: "--:--", done: false },
      ],
      progress: 30,
    },
    {
      id: "#LN236NBB9R",
      status: "Checking",
      statusColor: "#D97706",
      statusBg: "#FFFBEB",
      active: false,
      steps: [
        { date: "23 Jan", label: "Checking", time: "09:28 AM", done: true },
        { date: "27 Jan", label: "In transit", time: "--:--", done: false },
        { date: "1 Feb", label: "Delivered", time: "--:--", done: false },
      ],
      progress: 30,
    },
  ];

  stats: StatItem[] = [];
  vehicleInfo: VehicleInfo[] = [];
  orderDetails: OrderDetail[] = [];
  driverInfo: DriverInfo[] = [];
  customerInfo: CustomerInfo[] = [];

  activeOrder: Order;
  activeTab: string = "Vehicle";

  constructor(private sanitizer: DomSanitizer) {
    this.activeOrder = this.orders[0];
    this.initializeStats();
    this.initializeVehicleInfo();
    this.initializeOrderDetails();
    this.initializeDriverInfo();
    this.initializeCustomerInfo();
  }

  ngOnInit(): void {}

  initializeStats(): void {
    this.stats = [
      { 
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/>
          <circle cx="12" cy="9" r="3"/>
        </svg>`), 
        label: "Current location", 
        value: "Torstraße 10117" 
      },
      { 
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M13 2L3 14h8l-2 8 10-12h-8l2-8z"/>
        </svg>`), 
        label: "Speed", 
        value: "60 km/h" 
      },
      { 
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M2 12h20M12 2v20"/>
        </svg>`), 
        label: "Kilometers left", 
        value: "24 km" 
      },
      { 
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polygon points="12 2 2 7 12 12 22 7 12 2"/>
          <polyline points="2 17 12 22 22 17"/>
          <polyline points="2 12 12 17 22 12"/>
        </svg>`), 
        label: "Last stop", 
        value: "2 hours ago" 
      },
    ];
  }

  initializeVehicleInfo(): void {
    this.vehicleInfo = [
      { label: "MODEL", value: "Cargo Track HD320" },
      { label: "SPACE", value: "71% / 100%" },
      { label: "WEIGHT", value: "7,260 kg" },
      { label: "LOAD VOLUME", value: "372.45 in²" },
    ];
  }

  initializeOrderDetails(): void {
    this.orderDetails = [
      { label: "ORDER ID", value: this.activeOrder.id },
      { label: "STATUS", value: this.activeOrder.status },
      { label: "ORIGIN", value: "Mohrenstrasse 37, 10117 Berlin" },
      { label: "DESTINATION", value: "Goethestraße 1, 10115 Berlin" },
      { label: "CREATED", value: "21 January 2025" },
      { label: "ETA", value: "25 January 2025" },
    ];
  }

  initializeDriverInfo(): void {
    this.driverInfo = [
      { label: "NAME", value: "Max Klinger" },
      { label: "RATING", value: "4.9" },
      { label: "PHONE", value: "+49 123 456 789" },
      { label: "EXPERIENCE", value: "6 years" },
      { label: "LICENSE", value: "B-CE 44921" },
      { label: "DELIVERIES", value: "1,240" },
    ];
  }

  initializeCustomerInfo(): void {
    this.customerInfo = [
      { label: "COMPANY", value: "Berlin Logistics GmbH" },
      { label: "CONTACT", value: "Anna Bauer" },
      { label: "EMAIL", value: "a.bauer@bln-log.de" },
      { label: "PHONE", value: "+49 30 987 654" },
      { label: "ADDRESS", value: "Goethestraße 1, 10115 Berlin" },
      { label: "ACCOUNT", value: "#CUS-88210" },
    ];
  }

  setActiveOrder(order: Order): void {
    this.activeOrder = order;
    this.initializeOrderDetails();
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  onSearch(event: Event): void {
    const searchTerm = (event.target as HTMLInputElement).value;
    console.log('Searching for:', searchTerm);
  }
}