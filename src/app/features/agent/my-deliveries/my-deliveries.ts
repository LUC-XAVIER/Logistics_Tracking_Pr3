import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { RouterModule, Router } from '@angular/router';
import { SidebarAgentComponent } from '../../../shared/sidebar-agent/sidebar-agent';

interface DeliveryTask {
  id: string;
  trackingNumber: string;
  customerName: string;
  customerPhone: string;
  pickupAddress: string;
  deliveryAddress: string;
  status: 'PENDING' | 'IN_TRANSIT' | 'DELIVERED';
  statusColor: string;
  statusBg: string;
  fragilityLevel: number;
  weight: number;
  distance: number;
  estimatedTime: string;
  progress: number;
  priority: 'high' | 'medium' | 'low';
  scheduledTime?: Date;
  specialInstructions?: string;
}

interface StatCard {
  label: string;
  value: number;
  icon: SafeHtml;
  color: string;
  bgColor: string;
  trend?: number;
}

@Component({
  selector: 'app-my-deliveries',
  standalone: true,
  templateUrl: './my-deliveries.html',
  styleUrls: ['./my-deliveries.css'],
  imports: [CommonModule, FormsModule, RouterModule, SidebarAgentComponent]
})
export class MyDeliveriesComponent implements OnInit {
  Math = Math;
  
  // Agent information
  agentName = 'Max Klinger';
  agentAvatar = 'MK';
  
  // Deliveries data
  deliveries: DeliveryTask[] = [
    {
      id: '1',
      trackingNumber: 'AD345Jk758',
      customerName: 'Anna Bauer',
      customerPhone: '+49 30 987 654',
      pickupAddress: 'Berlin Central Hub, Mohrenstrasse 37, 10117 Berlin',
      deliveryAddress: 'Goethestraße 1, 10115 Berlin',
      status: 'IN_TRANSIT',
      statusColor: '#2563EB',
      statusBg: '#EFF6FF',
      fragilityLevel: 3,
      weight: 5.5,
      distance: 12.5,
      estimatedTime: '25 min',
      progress: 66,
      priority: 'medium',
      scheduledTime: new Date('2025-01-21T14:00:00'),
      specialInstructions: 'Call customer 10 minutes before arrival'
    },
    {
      id: '2',
      trackingNumber: 'FR156KL89K',
      customerName: 'Thomas Brown',
      customerPhone: '+49 123 456 7897',
      pickupAddress: 'Hamburg Port Agency, Hafenstraße 15, 20359 Hamburg',
      deliveryAddress: 'Rheinauhafen 23, 50678 Cologne',
      status: 'PENDING',
      statusColor: '#D97706',
      statusBg: '#FFFBEB',
      fragilityLevel: 7,
      weight: 3.2,
      distance: 8.3,
      estimatedTime: '18 min',
      progress: 0,
      priority: 'high',
      scheduledTime: new Date('2025-01-21T15:30:00'),
      specialInstructions: 'Handle with care - fragile items'
    },
    {
      id: '3',
      trackingNumber: 'LN236NBB9R',
      customerName: 'Sarah Wilson',
      customerPhone: '+49 123 456 7896',
      pickupAddress: 'Frankfurt Airport Logistics, Flughafenstraße 45, 60549 Frankfurt',
      deliveryAddress: 'Berlin Central Hub, Mohrenstrasse 37, 10117 Berlin',
      status: 'PENDING',
      statusColor: '#D97706',
      statusBg: '#FFFBEB',
      fragilityLevel: 2,
      weight: 8.0,
      distance: 15.2,
      estimatedTime: '32 min',
      progress: 0,
      priority: 'low',
      scheduledTime: new Date('2025-01-21T16:45:00')
    },
    {
      id: '4',
      trackingNumber: 'HY789MN12K',
      customerName: 'Emma Davis',
      customerPhone: '+49 123 456 7890',
      pickupAddress: 'Munich Logistics Center, Goethestraße 1, 80336 Munich',
      deliveryAddress: 'Cologne Rhein Hub, Rheinauhafen 23, 50678 Cologne',
      status: 'PENDING',
      statusColor: '#D97706',
      statusBg: '#FFFBEB',
      fragilityLevel: 9,
      weight: 12.5,
      distance: 45.8,
      estimatedTime: '55 min',
      progress: 0,
      priority: 'high',
      scheduledTime: new Date('2025-01-22T09:00:00'),
      specialInstructions: 'Very fragile - double box required'
    }
  ];

  filteredDeliveries: DeliveryTask[] = [];
  selectedDelivery: DeliveryTask | null = null;
  showInstructionsModal: boolean = false;
  
  searchTerm: string = '';
  filterStatus: string = 'ALL';
  filterPriority: string = 'ALL';
  sortBy: string = 'scheduledTime';
  sortOrder: 'asc' | 'desc' = 'asc';
  activeTab: string = 'all';
  
  // Stats
  statsCards: StatCard[] = [];
  
  constructor(private sanitizer: DomSanitizer, private router: Router) {
    this.filteredDeliveries = [...this.deliveries];
    this.calculateStats();
  }
  
  ngOnInit(): void {}
  
  calculateStats(): void {
    const totalDeliveries = this.deliveries.length;
    const pendingCount = this.deliveries.filter(d => d.status === 'PENDING').length;
    const inTransitCount = this.deliveries.filter(d => d.status === 'IN_TRANSIT').length;
    const highPriorityCount = this.deliveries.filter(d => d.priority === 'high').length;
    
    this.statsCards = [
      {
        label: 'Total Deliveries',
        value: totalDeliveries,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
          <path d="M1 3h15v13H1zM16 8h4l3 3v5h-7V8z"/>
          <circle cx="5.5" cy="18.5" r="2.5"/>
          <circle cx="18.5" cy="18.5" r="2.5"/>
        </svg>`),
        color: '#3B82F6',
        bgColor: '#EFF6FF'
      },
      {
        label: 'Pending',
        value: pendingCount,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
          <circle cx="12" cy="12" r="10"/>
          <polyline points="12 6 12 12 16 14"/>
        </svg>`),
        color: '#F59E0B',
        bgColor: '#FEF3C7'
      },
      {
        label: 'In Transit',
        value: inTransitCount,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
          <path d="M1 3h15v13H1zM16 8h4l3 3v5h-7V8z"/>
          <circle cx="5.5" cy="18.5" r="2.5"/>
          <circle cx="18.5" cy="18.5" r="2.5"/>
        </svg>`),
        color: '#10B981',
        bgColor: '#D1FAE5'
      },
      {
        label: 'High Priority',
        value: highPriorityCount,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2">
          <polygon points="12 2 2 7 12 12 22 7 12 2"/>
        </svg>`),
        color: '#EF4444',
        bgColor: '#FEE2E2'
      }
    ];
  }
  
  filterDeliveries(): void {
    this.filteredDeliveries = this.deliveries.filter(delivery => {
      const matchesSearch = this.searchTerm === '' || 
        delivery.trackingNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        delivery.customerName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        delivery.deliveryAddress.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesStatus = this.filterStatus === 'ALL' || delivery.status === this.filterStatus;
      const matchesPriority = this.filterPriority === 'ALL' || delivery.priority === this.filterPriority;
      const matchesTab = this.activeTab === 'all' || delivery.status === this.activeTab.toUpperCase();
      
      return matchesSearch && matchesStatus && matchesPriority && matchesTab;
    });
    
    this.sortDeliveries();
  }
  
  sortDeliveries(): void {
    this.filteredDeliveries.sort((a, b) => {
      let aVal: any;
      let bVal: any;
      
      switch(this.sortBy) {
        case 'trackingNumber':
          aVal = a.trackingNumber;
          bVal = b.trackingNumber;
          break;
        case 'customerName':
          aVal = a.customerName;
          bVal = b.customerName;
          break;
        case 'distance':
          aVal = a.distance;
          bVal = b.distance;
          break;
        case 'priority':
          const priorityOrder = { high: 0, medium: 1, low: 2 };
          aVal = priorityOrder[a.priority];
          bVal = priorityOrder[b.priority];
          break;
        case 'scheduledTime':
          aVal = a.scheduledTime?.getTime() || 0;
          bVal = b.scheduledTime?.getTime() || 0;
          break;
        default:
          aVal = a.scheduledTime?.getTime() || 0;
          bVal = b.scheduledTime?.getTime() || 0;
      }
      
      if (this.sortOrder === 'asc') {
        return aVal > bVal ? 1 : -1;
      } else {
        return aVal < bVal ? 1 : -1;
      }
    });
  }
  
  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.filterDeliveries();
  }
  
  onStatusFilter(event: Event): void {
    this.filterStatus = (event.target as HTMLSelectElement).value;
    this.filterDeliveries();
  }
  
  onPriorityFilter(event: Event): void {
    this.filterPriority = (event.target as HTMLSelectElement).value;
    this.filterDeliveries();
  }
  
  onSortChange(event: Event): void {
    this.sortBy = (event.target as HTMLSelectElement).value;
    this.sortDeliveries();
  }
  
  toggleSortOrder(): void {
    this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    this.sortDeliveries();
  }
  
  clearFilters(): void {
    this.searchTerm = '';
    this.filterStatus = 'ALL';
    this.filterPriority = 'ALL';
    this.activeTab = 'all';
    this.filterDeliveries();
  }
  
  setActiveTab(tab: string): void {
    this.activeTab = tab;
    this.filterDeliveries();
  }
  
  selectDelivery(delivery: DeliveryTask): void {
    this.selectedDelivery = delivery;
  }
  
  startDelivery(delivery: DeliveryTask): void {
    this.router.navigate(['/agent/live-tracking']);
  }
  
  viewInstructions(delivery: DeliveryTask): void {
    this.selectedDelivery = delivery;
    this.showInstructionsModal = true;
  }
  
  closeInstructionsModal(): void {
    this.showInstructionsModal = false;
    this.selectedDelivery = null;
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
  
  getPriorityText(priority: string): string {
    switch(priority) {
      case 'high': return 'High';
      case 'medium': return 'Medium';
      case 'low': return 'Low';
      default: return 'Medium';
    }
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
  
  formatTime(date?: Date): string {
    if (!date) return 'Not scheduled';
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
  
  formatDate(date?: Date): string {
    if (!date) return 'Not scheduled';
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }
}