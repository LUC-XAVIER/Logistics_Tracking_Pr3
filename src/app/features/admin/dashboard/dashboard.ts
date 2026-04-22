import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SidebarComponent } from '../../../shared/sidebar/sidebar';
import * as L from 'leaflet';

// Fix Leaflet default icon issue
const iconRetinaUrl = 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png';
const iconUrl = 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png';
const shadowUrl = 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png';
const iconDefault = L.icon({
  iconRetinaUrl,
  iconUrl,
  shadowUrl,
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  tooltipAnchor: [16, -28],
  shadowSize: [41, 41]
});
L.Marker.prototype.options.icon = iconDefault;

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
export class AdminDashboardComponent implements OnInit, AfterViewInit {
  private map: any;
  private currentMarker: any;
  private routeLine: any;

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

  // Route coordinates for Berlin
  private pickupCoords: [number, number] = [52.5123, 13.3889];
  private deliveryCoords: [number, number] = [52.5240, 13.4100];
  private currentCoords: [number, number] = [52.5163, 13.3990];
  private routePoints: [number, number][] = [
    [52.5123, 13.3889], // Pickup
    [52.5140, 13.3920],
    [52.5160, 13.3950],
    [52.5180, 13.3980],
    [52.5200, 13.4010],
    [52.5220, 13.4050],
    [52.5240, 13.4100]  // Delivery
  ];

  constructor(private sanitizer: DomSanitizer) {
    this.activeOrder = this.orders[0];
    this.initializeStats();
    this.initializeVehicleInfo();
    this.initializeOrderDetails();
    this.initializeDriverInfo();
    this.initializeCustomerInfo();
  }

  ngOnInit(): void {}

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {
    // Initialize map centered on Berlin
    this.map = L.map('map').setView([52.5200, 13.4050], 13);

    // Add OpenStreetMap tiles (FREE!)
    L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> &copy; <a href="https://carto.com/attributions">CARTO</a>',
      subdomains: 'abcd',
      maxZoom: 19,
      minZoom: 3
    }).addTo(this.map);

    // Add pickup marker (Green)
    const pickupMarker = L.marker(this.pickupCoords)
      .bindPopup(`
        <div style="font-family: 'DM Sans', sans-serif; min-width: 150px;">
          <strong style="color: #10B981;">📦 Pickup Location</strong><br/>
          Berlin Central Hub<br/>
          Mohrenstrasse 37, 10117 Berlin<br/>
          <span style="font-size: 11px; color: #6B7280;">Ready for pickup</span>
        </div>
      `)
      .addTo(this.map);

    // Add delivery marker (Red)
    const deliveryMarker = L.marker(this.deliveryCoords)
      .bindPopup(`
        <div style="font-family: 'DM Sans', sans-serif; min-width: 150px;">
          <strong style="color: #EF4444;">📍 Delivery Location</strong><br/>
          Goethestraße 1, 10115 Berlin<br/>
          <span style="font-size: 11px; color: #6B7280;">Customer: Anna Bauer</span>
        </div>
      `)
      .addTo(this.map);

    // Add current position marker (Blue, animated)
    const currentIcon = L.divIcon({
      className: 'custom-div-icon',
      html: `<div style="background-color: #3B82F6; width: 14px; height: 14px; border-radius: 50%; border: 2px solid white; box-shadow: 0 0 0 2px #3B82F6; animation: pulse 1.5s infinite;"></div>`,
      iconSize: [14, 14],
      popupAnchor: [0, -7]
    });

    this.currentMarker = L.marker(this.currentCoords, { icon: currentIcon })
      .bindPopup(`
        <div style="font-family: 'DM Sans', sans-serif;">
          <strong>🚚 Current Position</strong><br/>
          In Transit - ${this.activeOrder.progress}% complete<br/>
          ETA: 30 minutes
        </div>
      `)
      .addTo(this.map);

    // Draw route line with dashed style
    this.routeLine = L.polyline(this.routePoints, {
      color: '#3B82F6',
      weight: 4,
      opacity: 0.8,
      dashArray: '10, 10'
    }).addTo(this.map);

    // Add custom CSS animation for pulse effect
    const style = document.createElement('style');
    style.textContent = `
      @keyframes pulse {
        0% { transform: scale(1); opacity: 1; }
        50% { transform: scale(1.3); opacity: 0.7; }
        100% { transform: scale(1); opacity: 1; }
      }
      .custom-div-icon div {
        animation: pulse 1.5s infinite;
      }
    `;
    document.head.appendChild(style);

    // Fit map to show all markers
    const bounds = L.latLngBounds(this.routePoints);
    this.map.fitBounds(bounds, { padding: [50, 50] });
  }

  // Public methods for map controls
  zoomIn(): void {
    if (this.map) {
      this.map.zoomIn();
    }
  }

  zoomOut(): void {
    if (this.map) {
      this.map.zoomOut();
    }
  }

  centerMap(): void {
    if (this.map) {
      const bounds = L.latLngBounds(this.routePoints);
      this.map.fitBounds(bounds, { padding: [50, 50] });
    }
  }

  // Update current position based on progress
  updateCurrentPosition(progress: number): void {
    if (!this.currentMarker) return;
    
    const index = Math.floor(progress / 100 * (this.routePoints.length - 1));
    if (index < this.routePoints.length) {
      const newCoords = this.routePoints[index];
      this.currentMarker.setLatLng(newCoords);
      this.currentMarker.bindPopup(`
        <div style="font-family: 'DM Sans', sans-serif;">
          <strong>🚚 Current Position</strong><br/>
          In Transit - ${progress}% complete<br/>
          ETA: ${Math.max(0, Math.round(30 * (1 - progress / 100)))} minutes
        </div>
      `);
    }
  }

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
    // Update map progress
    this.updateCurrentPosition(order.progress);
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  onSearch(event: Event): void {
    const searchTerm = (event.target as HTMLInputElement).value;
    console.log('Searching for:', searchTerm);
  }
}