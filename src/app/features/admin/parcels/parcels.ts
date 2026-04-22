import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SidebarComponent } from '../../../shared/sidebar/sidebar';

interface Parcel {
  id: string;
  trackingNumber: string;
  senderName: string;
  receiverName: string;
  origin: string;
  destination: string;
  weight: number;
  fragilityLevel: number;
  status: 'CREATED' | 'PAID' | 'IN_TRANSIT' | 'DELIVERED' | 'CANCELLED';
  statusColor: string;
  statusBg: string;
  estimatedDelivery: Date;
  createdAt: Date;
  cost: number;
  assignedAgent?: string;
  progress: number;
  currentLocation?: string;
}

interface StatCard {
  label: string;
  value: number;
  icon: SafeHtml;
  color: string;
  bgColor: string;
}

@Component({
  selector: 'app-admin-parcels',
  standalone: true,
  templateUrl: './parcels.html',
  styleUrls: ['./parcels.css'],
  imports: [CommonModule, FormsModule, SidebarComponent]
})
export class AdminParcelsComponent implements OnInit {
  // Sample parcels data
  parcels: Parcel[] = [
    {
      id: '1',
      trackingNumber: 'AD345Jk758',
      senderName: 'John Doe',
      receiverName: 'Jane Smith',
      origin: 'Berlin Central Hub',
      destination: 'Munich Logistics Center',
      weight: 5.5,
      fragilityLevel: 3,
      status: 'IN_TRANSIT',
      statusColor: '#2563EB',
      statusBg: '#EFF6FF',
      estimatedDelivery: new Date('2025-01-25'),
      createdAt: new Date('2025-01-21'),
      cost: 45.50,
      assignedAgent: 'Max Klinger',
      progress: 66,
      currentLocation: 'Nuremberg'
    },
    {
      id: '2',
      trackingNumber: 'FR156KL89K',
      senderName: 'Alice Johnson',
      receiverName: 'Bob Williams',
      origin: 'Hamburg Port Agency',
      destination: 'Cologne Rhein Hub',
      weight: 3.2,
      fragilityLevel: 7,
      status: 'PAID',
      statusColor: '#D97706',
      statusBg: '#FFFBEB',
      estimatedDelivery: new Date('2025-01-26'),
      createdAt: new Date('2025-01-22'),
      cost: 67.80,
      assignedAgent: 'Not assigned',
      progress: 30,
      currentLocation: 'Hamburg'
    },
    {
      id: '3',
      trackingNumber: 'LN236NBB9R',
      senderName: 'Peter Schmidt',
      receiverName: 'Maria Garcia',
      origin: 'Frankfurt Airport Logistics',
      destination: 'Berlin Central Hub',
      weight: 8.0,
      fragilityLevel: 2,
      status: 'CREATED',
      statusColor: '#6B7280',
      statusBg: '#F3F4F6',
      estimatedDelivery: new Date('2025-01-30'),
      createdAt: new Date('2025-01-23'),
      cost: 52.30,
      assignedAgent: 'Not assigned',
      progress: 0,
      currentLocation: 'Frankfurt'
    },
    {
      id: '4',
      trackingNumber: 'HY789MN12K',
      senderName: 'Thomas Brown',
      receiverName: 'Sarah Wilson',
      origin: 'Berlin Central Hub',
      destination: 'Hamburg Port Agency',
      weight: 12.5,
      fragilityLevel: 9,
      status: 'IN_TRANSIT',
      statusColor: '#2563EB',
      statusBg: '#EFF6FF',
      estimatedDelivery: new Date('2025-01-24'),
      createdAt: new Date('2025-01-20'),
      cost: 98.75,
      assignedAgent: 'Klaus Richter',
      progress: 85,
      currentLocation: 'Lübeck'
    },
    {
      id: '5',
      trackingNumber: 'ZT234PO89M',
      senderName: 'Emma Davis',
      receiverName: 'Liam Martinez',
      origin: 'Munich Logistics Center',
      destination: 'Cologne Rhein Hub',
      weight: 2.8,
      fragilityLevel: 1,
      status: 'DELIVERED',
      statusColor: '#10B981',
      statusBg: '#D1FAE5',
      estimatedDelivery: new Date('2025-01-22'),
      createdAt: new Date('2025-01-19'),
      cost: 38.90,
      assignedAgent: 'Anna Weber',
      progress: 100,
      currentLocation: 'Cologne'
    },
    {
      id: '6',
      trackingNumber: 'QW567RT34Y',
      senderName: 'James Wilson',
      receiverName: 'Sophie Turner',
      origin: 'Cologne Rhein Hub',
      destination: 'Frankfurt Airport Logistics',
      weight: 6.7,
      fragilityLevel: 5,
      status: 'CANCELLED',
      statusColor: '#EF4444',
      statusBg: '#FEE2E2',
      estimatedDelivery: new Date('2025-01-28'),
      createdAt: new Date('2025-01-18'),
      cost: 0,
      assignedAgent: 'Not assigned',
      progress: 0,
      currentLocation: 'Cologne'
    }
  ];

  filteredParcels: Parcel[] = [];
  selectedParcel: Parcel | null = null;
  showStatusModal: boolean = false;
  showDetailsModal: boolean = false;
  searchTerm: string = '';
  selectedStatus: string = '';
  selectedFragility: string = '';
  sortBy: string = 'createdAt';
  sortOrder: 'asc' | 'desc' = 'desc';
  activeTab: string = 'all';

  // Status options for filter
  statusOptions: string[] = ['ALL', 'CREATED', 'PAID', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED'];
  fragilityOptions: string[] = ['ALL', 'Low (1-3)', 'Medium (4-7)', 'High (8-10)'];

  // Stats cards
  statsCards: StatCard[] = [];

  // New status for update
  newStatus: string = '';

  constructor(private sanitizer: DomSanitizer) {
    this.filteredParcels = [...this.parcels];
    this.calculateStats();
  }

  ngOnInit(): void {}

  calculateStats(): void {
    this.statsCards = [
      {
        label: 'Total Parcels',
        value: this.parcels.length,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
          <rect x="2" y="7" width="20" height="14" rx="2" />
          <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
        </svg>`),
        color: '#3B82F6',
        bgColor: '#EFF6FF'
      },
      {
        label: 'In Transit',
        value: this.parcels.filter(p => p.status === 'IN_TRANSIT').length,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2563EB" stroke-width="2">
          <path d="M1 3h15v13H1zM16 8h4l3 3v5h-7V8z" />
          <circle cx="5.5" cy="18.5" r="2.5" />
          <circle cx="18.5" cy="18.5" r="2.5" />
        </svg>`),
        color: '#2563EB',
        bgColor: '#DBEAFE'
      },
      {
        label: 'Delivered',
        value: this.parcels.filter(p => p.status === 'DELIVERED').length,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
        </svg>`),
        color: '#10B981',
        bgColor: '#D1FAE5'
      },
      {
        label: 'Cancelled',
        value: this.parcels.filter(p => p.status === 'CANCELLED').length,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2">
          <circle cx="12" cy="12" r="10" />
          <line x1="18" y1="6" x2="6" y2="18" />
          <line x1="6" y1="6" x2="18" y2="18" />
        </svg>`),
        color: '#EF4444',
        bgColor: '#FEE2E2'
      },
      {
        label: 'Avg Fragility',
        value: Math.round(this.parcels.reduce((sum, p) => sum + p.fragilityLevel, 0) / this.parcels.length),
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
          <polygon points="12 2 2 7 12 12 22 7 12 2" />
          <polyline points="2 17 12 22 22 17" />
          <polyline points="2 12 12 17 22 12" />
        </svg>`),
        color: '#F59E0B',
        bgColor: '#FEF3C7'
      },
      {
        label: 'Revenue',
        value: this.parcels.reduce((sum, p) => sum + p.cost, 0),
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="2">
          <circle cx="12" cy="12" r="10" />
          <path d="M12 2v20M2 12h20" />
        </svg>`),
        color: '#8B5CF6',
        bgColor: '#F3E8FF'
      }
    ];
  }

  getStatusIcon(status: string): SafeHtml {
    const icons: any = {
      'CREATED': this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
        <polyline points="14 2 14 8 20 8" />
        <line x1="16" y1="13" x2="8" y2="13" />
        <line x1="16" y1="17" x2="8" y2="17" />
      </svg>`),
      'PAID': this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10" />
        <path d="M12 2v20M2 12h20" />
      </svg>`),
      'IN_TRANSIT': this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M1 3h15v13H1zM16 8h4l3 3v5h-7V8z" />
        <circle cx="5.5" cy="18.5" r="2.5" />
        <circle cx="18.5" cy="18.5" r="2.5" />
      </svg>`),
      'DELIVERED': this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
      </svg>`),
      'CANCELLED': this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10" />
        <line x1="18" y1="6" x2="6" y2="18" />
        <line x1="6" y1="6" x2="18" y2="18" />
      </svg>`)
    };
    return icons[status] || icons['CREATED'];
  }

  filterParcels(): void {
    this.filteredParcels = this.parcels.filter(parcel => {
      const matchesSearch = this.searchTerm === '' || 
        parcel.trackingNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        parcel.senderName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        parcel.receiverName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        parcel.origin.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        parcel.destination.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesStatus = this.selectedStatus === '' || this.selectedStatus === 'ALL' || 
        parcel.status === this.selectedStatus;
      
      let matchesFragility = true;
      if (this.selectedFragility === 'Low (1-3)') {
        matchesFragility = parcel.fragilityLevel >= 1 && parcel.fragilityLevel <= 3;
      } else if (this.selectedFragility === 'Medium (4-7)') {
        matchesFragility = parcel.fragilityLevel >= 4 && parcel.fragilityLevel <= 7;
      } else if (this.selectedFragility === 'High (8-10)') {
        matchesFragility = parcel.fragilityLevel >= 8 && parcel.fragilityLevel <= 10;
      }
      
      let matchesTab = true;
      if (this.activeTab !== 'all') {
        matchesTab = parcel.status === this.activeTab.toUpperCase();
      }
      
      return matchesSearch && matchesStatus && matchesFragility && matchesTab;
    });

    this.sortParcels();
  }

  sortParcels(): void {
    this.filteredParcels.sort((a, b) => {
      let aVal: any;
      let bVal: any;
      
      switch(this.sortBy) {
        case 'trackingNumber':
          aVal = a.trackingNumber;
          bVal = b.trackingNumber;
          break;
        case 'status':
          aVal = a.status;
          bVal = b.status;
          break;
        case 'weight':
          aVal = a.weight;
          bVal = b.weight;
          break;
        case 'fragilityLevel':
          aVal = a.fragilityLevel;
          bVal = b.fragilityLevel;
          break;
        case 'cost':
          aVal = a.cost;
          bVal = b.cost;
          break;
        case 'createdAt':
          aVal = a.createdAt.getTime();
          bVal = b.createdAt.getTime();
          break;
        default:
          aVal = a.createdAt.getTime();
          bVal = b.createdAt.getTime();
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
    this.filterParcels();
  }

  onStatusFilter(event: Event): void {
    this.selectedStatus = (event.target as HTMLSelectElement).value;
    this.filterParcels();
  }

  onFragilityFilter(event: Event): void {
    this.selectedFragility = (event.target as HTMLSelectElement).value;
    this.filterParcels();
  }

  onSortChange(event: Event): void {
    this.sortBy = (event.target as HTMLSelectElement).value;
    this.sortParcels();
  }

  toggleSortOrder(): void {
    this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    this.sortParcels();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedStatus = '';
    this.selectedFragility = '';
    this.activeTab = 'all';
    this.filterParcels();
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
    this.filterParcels();
  }

  viewParcelDetails(parcel: Parcel): void {
    this.selectedParcel = parcel;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedParcel = null;
  }

  openStatusModal(parcel: Parcel): void {
    this.selectedParcel = parcel;
    this.newStatus = parcel.status;
    this.showStatusModal = true;
  }

  closeStatusModal(): void {
    this.showStatusModal = false;
    this.selectedParcel = null;
    this.newStatus = '';
  }

  updateParcelStatus(): void {
    if (this.selectedParcel && this.newStatus) {
      const index = this.parcels.findIndex(p => p.id === this.selectedParcel!.id);
      if (index !== -1) {
        this.parcels[index].status = this.newStatus as any;
        
        switch(this.newStatus) {
          case 'CREATED':
            this.parcels[index].progress = 0;
            break;
          case 'PAID':
            this.parcels[index].progress = 10;
            break;
          case 'IN_TRANSIT':
            this.parcels[index].progress = 50;
            break;
          case 'DELIVERED':
            this.parcels[index].progress = 100;
            break;
          case 'CANCELLED':
            this.parcels[index].progress = 0;
            break;
        }
        
        const statusColors: any = {
          'CREATED': { color: '#6B7280', bg: '#F3F4F6' },
          'PAID': { color: '#D97706', bg: '#FFFBEB' },
          'IN_TRANSIT': { color: '#2563EB', bg: '#EFF6FF' },
          'DELIVERED': { color: '#10B981', bg: '#D1FAE5' },
          'CANCELLED': { color: '#EF4444', bg: '#FEE2E2' }
        };
        
        this.parcels[index].statusColor = statusColors[this.newStatus].color;
        this.parcels[index].statusBg = statusColors[this.newStatus].bg;
        
        this.calculateStats();
        this.filterParcels();
      }
    }
    this.closeStatusModal();
  }

  deleteParcel(id: string): void {
    if (confirm('Are you sure you want to delete this parcel?')) {
      this.parcels = this.parcels.filter(p => p.id !== id);
      this.calculateStats();
      this.filterParcels();
      
      if (this.selectedParcel?.id === id) {
        this.closeDetailsModal();
      }
    }
  }

  getStatusColor(status: string): string {
    const colors: any = {
      'CREATED': '#6B7280',
      'PAID': '#D97706',
      'IN_TRANSIT': '#2563EB',
      'DELIVERED': '#10B981',
      'CANCELLED': '#EF4444'
    };
    return colors[status] || '#6B7280';
  }

  getStatusBgColor(status: string): string {
    const colors: any = {
      'CREATED': '#F3F4F6',
      'PAID': '#FFFBEB',
      'IN_TRANSIT': '#EFF6FF',
      'DELIVERED': '#D1FAE5',
      'CANCELLED': '#FEE2E2'
    };
    return colors[status] || '#F3F4F6';
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
}