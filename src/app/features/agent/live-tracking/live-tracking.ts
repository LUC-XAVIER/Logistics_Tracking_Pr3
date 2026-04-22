import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { SidebarAgentComponent } from '../../../shared/sidebar-agent/sidebar-agent';

interface RoutePoint {
  lat: number;
  lng: number;
  address: string;
  type: 'pickup' | 'delivery' | 'current';
  progress: number;
}

interface RouteSegment {
  type: 'town' | 'highway' | 'motorway';
  distance: number;
  duration: number;
  startPoint: string;
  endPoint: string;
  color: string;
  icon: SafeHtml;
}

interface DeliveryUpdate {
  timestamp: Date;
  location: string;
  status: string;
  message: string;
}

@Component({
  selector: 'app-live-tracking',
  standalone: true,
  templateUrl: './live-tracking.html',
  styleUrls: ['./live-tracking.css'],
  imports: [CommonModule, FormsModule, RouterModule, SidebarAgentComponent]
})
export class LiveTrackingComponent implements OnInit, OnDestroy {
  Math = Math;
  
  // Parcel information
  parcelId: string = '1';
  trackingNumber: string = 'AD345Jk758';
  customerName: string = 'Anna Bauer';
  customerPhone: string = '+49 30 987 654';
  deliveryAddress: string = 'Goethestraße 1, 10115 Berlin';
  pickupAddress: string = 'Berlin Central Hub, Mohrenstrasse 37';
  
  // Tracking state
  isTracking: boolean = false;
  isPaused: boolean = false;
  isDelivered: boolean = false;
  currentProgress: number = 66;
  currentSpeed: number = 45;
  baseSpeed: number = 60;
  fragilityLevel: number = 3;
  adjustedSpeed: number = 48;
  distanceRemaining: number = 24;
  totalDistance: number = 125;
  etaMinutes: number = 30;
  
  // Route points
  routePoints: RoutePoint[] = [
    { lat: 52.5123, lng: 13.3889, address: 'Berlin Central Hub', type: 'pickup', progress: 0 },
    { lat: 52.5200, lng: 13.4050, address: 'Current Position', type: 'current', progress: 66 },
    { lat: 52.5240, lng: 13.4100, address: 'Goethestraße 1, Berlin', type: 'delivery', progress: 100 }
  ];
  
  // Route segments
  routeSegments: RouteSegment[] = [];
  
  // Delivery updates
  deliveryUpdates: DeliveryUpdate[] = [
    { timestamp: new Date('2025-01-21T14:30:00'), location: 'Berlin Central Hub', status: 'Picked Up', message: 'Package picked up from warehouse' },
    { timestamp: new Date('2025-01-21T14:45:00'), location: 'A100 Entrance', status: 'In Transit', message: 'Entered highway' },
    { timestamp: new Date('2025-01-21T15:30:00'), location: 'A9 Motorway', status: 'In Transit', message: 'Making good progress' }
  ];
  
  // GPS simulation
  private simulationInterval: any;
  private progressInterval: number = 1000;
  private progressIncrement: number = 0.5;
  
  // UI state
  selectedSegment: RouteSegment | null = null;
  showSpeedModal: boolean = false;
  customSpeed: number = 45;
  manualLatitude: number = 52.5200;
  manualLongitude: number = 13.4050;
  
  constructor(private sanitizer: DomSanitizer) {
    this.calculateAdjustedSpeed();
    this.initializeRouteSegments();
  }
  
  ngOnInit(): void {
    console.log('Live tracking component initialized');
  }
  
  initializeRouteSegments(): void {
    this.routeSegments = [
      { 
        type: 'town', 
        distance: 5, 
        duration: 15, 
        startPoint: 'Berlin Hub', 
        endPoint: 'A100', 
        color: '#10B981', 
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
          <rect x="2" y="7" width="20" height="14" rx="2"/>
          <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
        </svg>`)
      },
      { 
        type: 'motorway', 
        distance: 110, 
        duration: 105, 
        startPoint: 'A100', 
        endPoint: 'A9 Munich', 
        color: '#3B82F6', 
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
          <rect x="2" y="7" width="13" height="10" rx="1"/>
          <path d="M15 9h3l3 3v5h-6V9z"/>
          <circle cx="6" cy="19" r="2"/>
          <circle cx="18" cy="19" r="2"/>
        </svg>`)
      },
      { 
        type: 'town', 
        distance: 10, 
        duration: 20, 
        startPoint: 'A9 Munich', 
        endPoint: 'Delivery Location', 
        color: '#10B981', 
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
          <rect x="2" y="7" width="20" height="14" rx="2"/>
          <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
        </svg>`)
      }
    ];
  }
  
  ngOnDestroy(): void {
    this.stopSimulation();
  }
  
  calculateAdjustedSpeed(): void {
    this.adjustedSpeed = this.baseSpeed * (1 - this.fragilityLevel / 15);
    if (this.isTracking && !this.isPaused) {
      this.currentSpeed = this.adjustedSpeed;
    }
  }
  
  getControlIcon(type: string): SafeHtml {
    const icons: any = {
      'play': this.sanitizer.bypassSecurityTrustHtml(`<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
        <polygon points="5 3 19 12 5 21 5 3"/>
      </svg>`),
      'pause': this.sanitizer.bypassSecurityTrustHtml(`<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2">
        <rect x="6" y="4" width="4" height="16"/>
        <rect x="14" y="4" width="4" height="16"/>
      </svg>`),
      'reset': this.sanitizer.bypassSecurityTrustHtml(`<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M23 4v6h-6M1 20v-6h6M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
      </svg>`)
    };
    return icons[type] || icons['play'];
  }
  
  startSimulation(): void {
    if (this.isDelivered) return;
    
    this.isTracking = true;
    this.isPaused = false;
    this.currentSpeed = this.adjustedSpeed;
    
    this.simulationInterval = setInterval(() => {
      if (!this.isPaused && this.currentProgress < 100) {
        this.currentProgress += this.progressIncrement;
        this.distanceRemaining = this.totalDistance * (1 - this.currentProgress / 100);
        this.etaMinutes = Math.max(0, Math.round(this.distanceRemaining / this.currentSpeed * 60));
        
        if (this.currentProgress >= 100) {
          this.completeDelivery();
        }
        
        this.updateCurrentLocation();
      }
    }, this.progressInterval);
  }
  
  pauseSimulation(): void {
    this.isPaused = true;
    if (this.simulationInterval) {
      clearInterval(this.simulationInterval);
      this.simulationInterval = null;
    }
  }
  
  resumeSimulation(): void {
    if (!this.isDelivered && this.isPaused) {
      this.isPaused = false;
      this.startSimulation();
    }
  }
  
  stopSimulation(): void {
    if (this.simulationInterval) {
      clearInterval(this.simulationInterval);
      this.simulationInterval = null;
    }
    this.isTracking = false;
    this.isPaused = false;
  }
  
  resetSimulation(): void {
    this.stopSimulation();
    this.currentProgress = 0;
    this.distanceRemaining = this.totalDistance;
    this.etaMinutes = Math.round(this.totalDistance / this.currentSpeed * 60);
    this.isDelivered = false;
  }
  
  completeDelivery(): void {
    this.stopSimulation();
    this.isDelivered = true;
    this.currentProgress = 100;
    this.distanceRemaining = 0;
    this.etaMinutes = 0;
    this.addUpdate('Delivery Location', 'Delivered', 'Package successfully delivered to customer');
  }
  
  markAsDelivered(): void {
    if (confirm('Mark this delivery as completed?')) {
      this.completeDelivery();
    }
  }
  
  updateCurrentLocation(): void {
    const lat = 52.5123 + (this.currentProgress / 100) * 0.0117;
    const lng = 13.3889 + (this.currentProgress / 100) * 0.0211;
    this.manualLatitude = lat;
    this.manualLongitude = lng;
  }
  
  updateManualPosition(): void {
    this.addUpdate('Manual Update', 'Location Updated', `Position set to (${this.manualLatitude}, ${this.manualLongitude})`);
  }
  
  updateSpeed(): void {
    this.currentSpeed = this.customSpeed;
    this.addUpdate('Speed Update', 'Speed Changed', `Delivery speed adjusted to ${this.currentSpeed} km/h`);
    this.showSpeedModal = false;
  }
  
  addUpdate(location: string, status: string, message: string): void {
    this.deliveryUpdates.unshift({
      timestamp: new Date(),
      location: location,
      status: status,
      message: message
    });
    
    if (this.deliveryUpdates.length > 20) {
      this.deliveryUpdates.pop();
    }
  }
  
  selectSegment(segment: RouteSegment): void {
    this.selectedSegment = segment;
  }
  
  closeSegmentModal(): void {
    this.selectedSegment = null;
  }
  
  openSpeedModal(): void {
    this.customSpeed = this.currentSpeed;
    this.showSpeedModal = true;
  }
  
  closeSpeedModal(): void {
    this.showSpeedModal = false;
  }
  
  getFragilityClass(level: number): string {
    if (level <= 3) return 'fragility-low';
    if (level <= 7) return 'fragility-medium';
    return 'fragility-high';
  }
  
  getFragilityText(level: number): string {
    if (level <= 3) return 'Low';
    if (level <= 7) return 'Medium';
    return 'High';
  }
  
  formatDuration(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours === 0) return `${mins} min`;
    return `${hours}h ${mins}m`;
  }
  
  formatTime(date: Date): string {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
}