import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { SidebarAgentComponent } from '../../../shared/sidebar-agent/sidebar-agent';

interface DeliveryTask {
  id: string;
  trackingNumber: string;
  pickupAddress: string;
  deliveryAddress: string;
  customerName: string;
  customerPhone: string;
  status: 'PENDING' | 'IN_TRANSIT' | 'DELIVERED';
  statusColor: string;
  statusBg: string;
  fragilityLevel: number;
  weight: number;
  distance: number;
  estimatedTime: string;
  progress: number;
  currentLocation?: string;
  priority: 'high' | 'medium' | 'low';
}

interface StatCard {
  label: string;
  value: number;
  icon: SafeHtml;
  color: string;
  bgColor: string;
  trend?: number;
  unit?: string;
}

interface ActivityLog {
  id: string;
  action: string;
  parcelId: string;
  timestamp: Date;
  status: string;
}

@Component({
  selector: 'app-agent-dashboard',
  standalone: true,
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
  imports: [CommonModule, FormsModule, RouterModule, SidebarAgentComponent]
})
export class AgentDashboardComponent implements OnInit {
  Math = Math;
  
  // Agent information
  agentName = 'Max Klinger';
  agentAvatar = 'MK';
  agentRating = 4.9;
  totalDeliveries = 1240;
  
  // Today's deliveries
  todayDeliveries: DeliveryTask[] = [
    {
      id: '1',
      trackingNumber: 'AD345Jk758',
      pickupAddress: 'Berlin Central Hub, Mohrenstrasse 37',
      deliveryAddress: 'Goethestraße 1, 10115 Berlin',
      customerName: 'Anna Bauer',
      customerPhone: '+49 30 987 654',
      status: 'IN_TRANSIT',
      statusColor: '#2563EB',
      statusBg: '#EFF6FF',
      fragilityLevel: 3,
      weight: 5.5,
      distance: 12.5,
      estimatedTime: '25 min',
      progress: 66,
      currentLocation: 'Torstraße 10117',
      priority: 'medium'
    },
    {
      id: '2',
      trackingNumber: 'FR156KL89K',
      pickupAddress: 'Hamburg Port Agency, Hafenstraße 15',
      deliveryAddress: 'Rheinauhafen 23, 50678 Cologne',
      customerName: 'Thomas Brown',
      customerPhone: '+49 123 456 7897',
      status: 'PENDING',
      statusColor: '#D97706',
      statusBg: '#FFFBEB',
      fragilityLevel: 7,
      weight: 3.2,
      distance: 8.3,
      estimatedTime: '18 min',
      progress: 0,
      priority: 'high'
    },
    {
      id: '3',
      trackingNumber: 'LN236NBB9R',
      pickupAddress: 'Frankfurt Airport Logistics, Flughafenstraße 45',
      deliveryAddress: 'Berlin Central Hub, Mohrenstrasse 37',
      customerName: 'Sarah Wilson',
      customerPhone: '+49 123 456 7896',
      status: 'PENDING',
      statusColor: '#D97706',
      statusBg: '#FFFBEB',
      fragilityLevel: 2,
      weight: 8.0,
      distance: 15.2,
      estimatedTime: '32 min',
      progress: 0,
      priority: 'low'
    }
  ];
  
  // Completed deliveries today
  completedToday = 2;
  
  // Statistics
  statsCards: StatCard[] = [];
  
  // Activity logs
  activityLogs: ActivityLog[] = [
    {
      id: '1',
      action: 'Parcel delivered successfully',
      parcelId: 'AD345Jk758',
      timestamp: new Date('2025-01-21T14:30:00'),
      status: 'DELIVERED'
    },
    {
      id: '2',
      action: 'Started delivery',
      parcelId: 'FR156KL89K',
      timestamp: new Date('2025-01-21T13:15:00'),
      status: 'IN_TRANSIT'
    },
    {
      id: '3',
      action: 'Picked up parcel',
      parcelId: 'LN236NBB9R',
      timestamp: new Date('2025-01-21T11:45:00'),
      status: 'PICKED_UP'
    },
    {
      id: '4',
      action: 'Completed delivery',
      parcelId: 'ZT234PO89M',
      timestamp: new Date('2025-01-21T10:30:00'),
      status: 'DELIVERED'
    }
  ];
  
  selectedTask: DeliveryTask | null = null;
  searchTerm: string = '';
  filterStatus: string = 'ALL';
  activeTab: string = 'today';
  
  constructor(private sanitizer: DomSanitizer) {
    this.calculateStats();
  }
  
  ngOnInit(): void {}
  
  calculateStats(): void {
    const pendingCount = this.todayDeliveries.filter(t => t.status === 'PENDING').length;
    const inTransitCount = this.todayDeliveries.filter(t => t.status === 'IN_TRANSIT').length;
    const deliveredCount = this.completedToday;
    const totalDistance = this.todayDeliveries.reduce((sum, t) => sum + t.distance, 0);
    
    this.statsCards = [
      {
        label: 'Today\'s Deliveries',
        value: this.todayDeliveries.length,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
          <path d="M1 3h15v13H1zM16 8h4l3 3v5h-7V8z"/>
          <circle cx="5.5" cy="18.5" r="2.5"/>
          <circle cx="18.5" cy="18.5" r="2.5"/>
        </svg>`),
        color: '#3B82F6',
        bgColor: '#EFF6FF',
        trend: 5
      },
      {
        label: 'Completed',
        value: deliveredCount,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
        </svg>`),
        color: '#10B981',
        bgColor: '#D1FAE5',
        trend: 12
      },
      {
        label: 'In Progress',
        value: inTransitCount,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
          <circle cx="12" cy="12" r="10"/>
          <polyline points="12 6 12 12 16 14"/>
        </svg>`),
        color: '#F59E0B',
        bgColor: '#FEF3C7',
        trend: -2
      },
      {
        label: 'Total Distance',
        value: Math.round(totalDistance),
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="2">
          <path d="M2 12h20M12 2v20"/>
        </svg>`),
        color: '#8B5CF6',
        bgColor: '#F3E8FF',
        unit: 'km'
      }
    ];
  }
  
  getFilteredDeliveries(): DeliveryTask[] {
    let filtered = this.todayDeliveries;
    
    if (this.searchTerm) {
      filtered = filtered.filter(task => 
        task.trackingNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        task.customerName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        task.deliveryAddress.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
    
    if (this.filterStatus !== 'ALL') {
      filtered = filtered.filter(task => task.status === this.filterStatus);
    }
    
    return filtered;
  }
  
  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
  }
  
  onStatusFilter(event: Event): void {
    this.filterStatus = (event.target as HTMLSelectElement).value;
  }
  
  clearFilters(): void {
    this.searchTerm = '';
    this.filterStatus = 'ALL';
  }
  
  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }
  
  selectTask(task: DeliveryTask): void {
    this.selectedTask = task;
  }
  
  startDelivery(task: DeliveryTask): void {
    console.log('Starting delivery for:', task.trackingNumber);
    // Navigate to live tracking
  }
  
  getPriorityIcon(priority: string): SafeHtml {
    const icons: any = {
      high: this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2">
        <polygon points="12 2 2 7 12 12 22 7 12 2"/>
      </svg>`),
      medium: this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
        <polygon points="12 2 2 7 12 12 22 7 12 2"/>
      </svg>`),
      low: this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
        <polygon points="12 2 2 7 12 12 22 7 12 2"/>
      </svg>`)
    };
    return icons[priority] || icons['medium'];
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
  
  formatTime(date: Date): string {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
  
  getStarIcon(): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
    </svg>`);
  }
}