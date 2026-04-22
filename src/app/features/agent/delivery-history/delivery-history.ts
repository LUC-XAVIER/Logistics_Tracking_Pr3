import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { SidebarAgentComponent } from '../../../shared/sidebar-agent/sidebar-agent';

interface CompletedDelivery {
  id: string;
  trackingNumber: string;
  customerName: string;
  customerPhone: string;
  deliveryAddress: string;
  pickupAddress: string;
  completedAt: Date;
  deliveryTime: number; // in minutes
  distance: number; // in km
  weight: number;
  fragilityLevel: number;
  earnings: number;
  rating: number;
  customerFeedback?: string;
  status: 'DELIVERED' | 'RETURNED';
  proofOfDelivery?: string;
}

interface StatsCard {
  label: string;
  value: number;
  icon: SafeHtml;
  color: string;
  bgColor: string;
  trend?: number;
  unit?: string;
}

@Component({
  selector: 'app-delivery-history',
  standalone: true,
  templateUrl: './delivery-history.html',
  styleUrls: ['./delivery-history.css'],
  imports: [CommonModule, FormsModule, RouterModule, SidebarAgentComponent]
})
export class DeliveryHistoryComponent implements OnInit {
  Math = Math;

  // Agent info
  agentName = 'Max Klinger';
  agentAvatar = 'MK';
  
  // Completed deliveries data
  completedDeliveries: CompletedDelivery[] = [
    {
      id: '1',
      trackingNumber: 'ZT234PO89M',
      customerName: 'Emma Davis',
      customerPhone: '+49 123 456 7890',
      deliveryAddress: 'Cologne Rhein Hub, Rheinauhafen 23, 50678 Cologne',
      pickupAddress: 'Munich Logistics Center, Goethestraße 1, 80336 Munich',
      completedAt: new Date('2025-01-21T14:30:00'),
      deliveryTime: 45,
      distance: 392,
      weight: 2.8,
      fragilityLevel: 1,
      earnings: 38.90,
      rating: 5,
      customerFeedback: 'Very fast delivery! The package arrived in perfect condition.',
      status: 'DELIVERED',
      proofOfDelivery: 'signature.jpg'
    },
    {
      id: '2',
      trackingNumber: 'HY789MN12K',
      customerName: 'Sarah Wilson',
      customerPhone: '+49 123 456 7891',
      deliveryAddress: 'Hamburg Port Agency, Hafenstraße 15, 20359 Hamburg',
      pickupAddress: 'Berlin Central Hub, Mohrenstrasse 37, 10117 Berlin',
      completedAt: new Date('2025-01-20T11:15:00'),
      deliveryTime: 195,
      distance: 289,
      weight: 12.5,
      fragilityLevel: 9,
      earnings: 98.75,
      rating: 4,
      customerFeedback: 'Careful handling of fragile items. Thank you!',
      status: 'DELIVERED'
    },
    {
      id: '3',
      trackingNumber: 'QW567RT34Y',
      customerName: 'Thomas Brown',
      customerPhone: '+49 123 456 7892',
      deliveryAddress: 'Frankfurt Airport Logistics, Flughafenstraße 45, 60549 Frankfurt',
      pickupAddress: 'Cologne Rhein Hub, Rheinauhafen 23, 50678 Cologne',
      completedAt: new Date('2025-01-19T16:45:00'),
      deliveryTime: 260,
      distance: 392,
      weight: 6.7,
      fragilityLevel: 5,
      earnings: 67.80,
      rating: 5,
      customerFeedback: 'Excellent service, very professional driver.',
      status: 'DELIVERED'
    },
    {
      id: '4',
      trackingNumber: 'AD345Jk758',
      customerName: 'Anna Bauer',
      customerPhone: '+49 123 456 7893',
      deliveryAddress: 'Goethestraße 1, 10115 Berlin',
      pickupAddress: 'Berlin Central Hub, Mohrenstrasse 37, 10117 Berlin',
      completedAt: new Date('2025-01-18T10:00:00'),
      deliveryTime: 25,
      distance: 12.5,
      weight: 5.5,
      fragilityLevel: 3,
      earnings: 45.50,
      rating: 5,
      customerFeedback: 'Quick and friendly delivery!',
      status: 'DELIVERED'
    },
    {
      id: '5',
      trackingNumber: 'FR156KL89K',
      customerName: 'Bob Williams',
      customerPhone: '+49 123 456 7894',
      deliveryAddress: 'Cologne Rhein Hub, Rheinauhafen 23, 50678 Cologne',
      pickupAddress: 'Hamburg Port Agency, Hafenstraße 15, 20359 Hamburg',
      completedAt: new Date('2025-01-17T13:20:00'),
      deliveryTime: 285,
      distance: 412,
      weight: 3.2,
      fragilityLevel: 7,
      earnings: 67.80,
      rating: 3,
      customerFeedback: 'A bit delayed but package was safe.',
      status: 'DELIVERED'
    },
    {
      id: '6',
      trackingNumber: 'LN236NBB9R',
      customerName: 'Maria Garcia',
      customerPhone: '+49 123 456 7895',
      deliveryAddress: 'Berlin Central Hub, Mohrenstrasse 37, 10117 Berlin',
      pickupAddress: 'Frankfurt Airport Logistics, Flughafenstraße 45, 60549 Frankfurt',
      completedAt: new Date('2025-01-16T15:30:00'),
      deliveryTime: 360,
      distance: 547,
      weight: 8.0,
      fragilityLevel: 2,
      earnings: 52.30,
      rating: 4,
      customerFeedback: 'Good communication throughout.',
      status: 'DELIVERED'
    }
  ];

  filteredDeliveries: CompletedDelivery[] = [];
  selectedDelivery: CompletedDelivery | null = null;
  showFeedbackModal: boolean = false;
  
  searchTerm: string = '';
  selectedRating: string = '';
  selectedMonth: string = '';
  sortBy: string = 'completedAt';
  sortOrder: 'asc' | 'desc' = 'desc';
  dateRange: string = 'all';
  
  // Stats
  statsCards: StatsCard[] = [];
  
  // Month options for filter
  monthOptions: string[] = ['ALL', 'January', 'February', 'March', 'April', 'May', 'June'];
  
  constructor(private sanitizer: DomSanitizer) {
    this.filteredDeliveries = [...this.completedDeliveries];
    this.calculateStats();
  }
  
  ngOnInit(): void {}
  
  calculateStats(): void {
    const totalDeliveries = this.completedDeliveries.length;
    const totalEarnings = this.completedDeliveries.reduce((sum, d) => sum + d.earnings, 0);
    const avgRating = this.completedDeliveries.reduce((sum, d) => sum + d.rating, 0) / totalDeliveries;
    const totalDistance = this.completedDeliveries.reduce((sum, d) => sum + d.distance, 0);
    const avgDeliveryTime = Math.round(this.completedDeliveries.reduce((sum, d) => sum + d.deliveryTime, 0) / totalDeliveries);
    
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
        bgColor: '#EFF6FF',
        trend: 15
      },
      {
        label: 'Total Earnings',
        value: totalEarnings,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
          <circle cx="12" cy="12" r="10"/>
          <path d="M12 2v20M2 12h20"/>
        </svg>`),
        color: '#10B981',
        bgColor: '#D1FAE5',
        trend: 12
      },
      {
        label: 'Avg Rating',
        value: avgRating,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
        </svg>`),
        color: '#F59E0B',
        bgColor: '#FEF3C7',
        trend: 5
      },
      {
        label: 'Total Distance',
        value: totalDistance,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="2">
          <path d="M2 12h20M12 2v20"/>
        </svg>`),
        color: '#8B5CF6',
        bgColor: '#F3E8FF',
        unit: 'km'
      },
      {
        label: 'Avg Delivery Time',
        value: avgDeliveryTime,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2">
          <circle cx="12" cy="12" r="10"/>
          <polyline points="12 6 12 12 16 14"/>
        </svg>`),
        color: '#EF4444',
        bgColor: '#FEE2E2',
        unit: 'min'
      }
    ];
  }
  
  filterDeliveries(): void {
    this.filteredDeliveries = this.completedDeliveries.filter(delivery => {
      const matchesSearch = this.searchTerm === '' || 
        delivery.trackingNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        delivery.customerName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        delivery.deliveryAddress.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesRating = this.selectedRating === '' || delivery.rating >= parseInt(this.selectedRating);
      
      let matchesMonth = true;
      if (this.selectedMonth !== '' && this.selectedMonth !== 'ALL') {
        const monthNames = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
        const deliveryMonth = monthNames[delivery.completedAt.getMonth()];
        matchesMonth = deliveryMonth === this.selectedMonth;
      }
      
      return matchesSearch && matchesRating && matchesMonth;
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
        case 'completedAt':
          aVal = a.completedAt.getTime();
          bVal = b.completedAt.getTime();
          break;
        case 'earnings':
          aVal = a.earnings;
          bVal = b.earnings;
          break;
        case 'rating':
          aVal = a.rating;
          bVal = b.rating;
          break;
        case 'distance':
          aVal = a.distance;
          bVal = b.distance;
          break;
        default:
          aVal = a.completedAt.getTime();
          bVal = b.completedAt.getTime();
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
  
  onRatingFilter(event: Event): void {
    this.selectedRating = (event.target as HTMLSelectElement).value;
    this.filterDeliveries();
  }
  
  onMonthFilter(event: Event): void {
    this.selectedMonth = (event.target as HTMLSelectElement).value;
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
    this.selectedRating = '';
    this.selectedMonth = '';
    this.filterDeliveries();
  }
  
  viewDeliveryDetails(delivery: CompletedDelivery): void {
    this.selectedDelivery = delivery;
  }
  
  closeDetails(): void {
    this.selectedDelivery = null;
  }
  
  viewFeedback(delivery: CompletedDelivery): void {
    this.selectedDelivery = delivery;
    this.showFeedbackModal = true;
  }
  
  closeFeedbackModal(): void {
    this.showFeedbackModal = false;
    this.selectedDelivery = null;
  }
  
  getStarIcon(): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
    </svg>`);
  }
  
  getEmptyStarIcon(): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#D1D5DB" stroke-width="2">
      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
    </svg>`);
  }
  
  getRatingStars(rating: number): string {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
      stars += i <= rating ? '★' : '☆';
    }
    return stars;
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
  
  formatCurrency(amount: number): string {
    return `€${amount.toFixed(2)}`;
  }
  
  formatDuration(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours === 0) return `${mins} min`;
    return `${hours}h ${mins}m`;
  }
}