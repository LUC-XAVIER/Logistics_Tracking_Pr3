import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SidebarComponent } from '../../../shared/sidebar/sidebar';

interface Agency {
  id: string;
  name: string;
  country: string;
  town: string;
  address: string;
  latitude: number;
  longitude: number;
  phone: string;
  email: string;
  status: 'active' | 'inactive';
  createdAt: Date;
  totalParcels?: number;
}

interface NavItem {
  id: string;
  svg: string;
  active?: boolean;
}

@Component({
  selector: 'app-admin-agencies',
  standalone: true,  // Add this since you're using standalone components
  templateUrl: './agencies.html',
  styleUrls: ['./agencies.css'],
  imports: [CommonModule, FormsModule, SidebarComponent]  // Import required modules here
})
export class AdminAgenciesComponent implements OnInit {
  // Navigation items (same as dashboard)
  navItems: NavItem[] = [
    {
      id: "grid",
      svg: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="3" width="7" height="7" rx="1" />
              <rect x="14" y="3" width="7" height="7" rx="1" />
              <rect x="3" y="14" width="7" height="7" rx="1" />
              <rect x="14" y="14" width="7" height="7" rx="1" />
            </svg>`
    },
    {
      id: "truck1",
      svg: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M1 3h15v13H1zM16 8h4l3 3v5h-7V8z" />
              <circle cx="5.5" cy="18.5" r="2.5" />
              <circle cx="18.5" cy="18.5" r="2.5" />
            </svg>`
    },
    {
      id: "truck2",
      svg: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="2" y="7" width="13" height="10" rx="1" />
              <path d="M15 9h3l3 3v5h-6V9z" />
              <circle cx="6" cy="19" r="2" />
              <circle cx="18" cy="19" r="2" />
            </svg>`
    },
    {
      id: "user",
      svg: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="8" r="4" />
              <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
            </svg>`
    },
    {
      id: "chat",
      svg: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>`
    },
  ];

  // Sample agencies data
  agencies: Agency[] = [
    {
      id: '1',
      name: 'Berlin Central Hub',
      country: 'Germany',
      town: 'Berlin',
      address: 'Mohrenstrasse 37, 10117 Berlin',
      latitude: 52.5123,
      longitude: 13.3889,
      phone: '+49 30 123456',
      email: 'berlin@logistics.com',
      status: 'active',
      createdAt: new Date('2024-01-15'),
      totalParcels: 1247
    },
    {
      id: '2',
      name: 'Munich Logistics Center',
      country: 'Germany',
      town: 'Munich',
      address: 'Goethestraße 1, 80336 Munich',
      latitude: 48.1351,
      longitude: 11.5820,
      phone: '+49 89 789012',
      email: 'munich@logistics.com',
      status: 'active',
      createdAt: new Date('2024-02-20'),
      totalParcels: 892
    },
    {
      id: '3',
      name: 'Hamburg Port Agency',
      country: 'Germany',
      town: 'Hamburg',
      address: 'Hafenstraße 15, 20359 Hamburg',
      latitude: 53.5511,
      longitude: 9.9937,
      phone: '+49 40 345678',
      email: 'hamburg@logistics.com',
      status: 'inactive',
      createdAt: new Date('2024-03-10'),
      totalParcels: 456
    },
    {
      id: '4',
      name: 'Cologne Rhein Hub',
      country: 'Germany',
      town: 'Cologne',
      address: 'Rheinauhafen 23, 50678 Cologne',
      latitude: 50.9375,
      longitude: 6.9603,
      phone: '+49 221 901234',
      email: 'cologne@logistics.com',
      status: 'active',
      createdAt: new Date('2024-04-05'),
      totalParcels: 678
    },
    {
      id: '5',
      name: 'Frankfurt Airport Logistics',
      country: 'Germany',
      town: 'Frankfurt',
      address: 'Flughafenstraße 45, 60549 Frankfurt',
      latitude: 50.1109,
      longitude: 8.6821,
      phone: '+49 69 567890',
      email: 'frankfurt@logistics.com',
      status: 'active',
      createdAt: new Date('2024-05-12'),
      totalParcels: 2345
    }
  ];

  filteredAgencies: Agency[] = [];
  selectedAgency: Agency | null = null;
  showModal: boolean = false;
  modalMode: 'add' | 'edit' = 'add';
  searchTerm: string = '';
  selectedCountry: string = '';
  selectedStatus: string = '';
  activeTab: string = 'agencies';

  // Form data for add/edit
  formData: Partial<Agency> = {
    name: '',
    country: '',
    town: '',
    address: '',
    latitude: 0,
    longitude: 0,
    phone: '',
    email: '',
    status: 'active'
  };

  countries: string[] = ['Germany', 'France', 'Italy', 'Spain', 'Netherlands', 'Belgium', 'Austria', 'Switzerland'];
  statuses: string[] = ['active', 'inactive'];

  // Stats for the dashboard
  stats = {
    totalAgencies: 0,
    activeAgencies: 0,
    totalParcelsHandled: 0,
    averageParcelsPerAgency: 0
  };

  constructor() {
    this.filteredAgencies = [...this.agencies];
    this.calculateStats();
  }

  ngOnInit(): void {}

  calculateStats(): void {
    this.stats.totalAgencies = this.agencies.length;
    this.stats.activeAgencies = this.agencies.filter(a => a.status === 'active').length;
    this.stats.totalParcelsHandled = this.agencies.reduce((sum, a) => sum + (a.totalParcels || 0), 0);
    this.stats.averageParcelsPerAgency = Math.round(this.stats.totalParcelsHandled / this.stats.totalAgencies);
  }

  filterAgencies(): void {
    this.filteredAgencies = this.agencies.filter(agency => {
      const matchesSearch = this.searchTerm === '' || 
        agency.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        agency.town.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        agency.address.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesCountry = this.selectedCountry === '' || agency.country === this.selectedCountry;
      const matchesStatus = this.selectedStatus === '' || agency.status === this.selectedStatus;
      
      return matchesSearch && matchesCountry && matchesStatus;
    });
  }

  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.filterAgencies();
  }

  onCountryFilter(event: Event): void {
    this.selectedCountry = (event.target as HTMLSelectElement).value;
    this.filterAgencies();
  }

  onStatusFilter(event: Event): void {
    this.selectedStatus = (event.target as HTMLSelectElement).value;
    this.filterAgencies();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedCountry = '';
    this.selectedStatus = '';
    this.filterAgencies();
  }

  openAddModal(): void {
    this.modalMode = 'add';
    this.formData = {
      name: '',
      country: '',
      town: '',
      address: '',
      latitude: 0,
      longitude: 0,
      phone: '',
      email: '',
      status: 'active'
    };
    this.showModal = true;
  }

  openEditModal(agency: Agency): void {
    this.modalMode = 'edit';
    this.formData = { ...agency };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedAgency = null;
  }

  saveAgency(): void {
    if (this.modalMode === 'add') {
      const newAgency: Agency = {
        id: (this.agencies.length + 1).toString(),
        name: this.formData.name!,
        country: this.formData.country!,
        town: this.formData.town!,
        address: this.formData.address!,
        latitude: this.formData.latitude!,
        longitude: this.formData.longitude!,
        phone: this.formData.phone!,
        email: this.formData.email!,
        status: this.formData.status as 'active' | 'inactive',
        createdAt: new Date(),
        totalParcels: 0
      };
      this.agencies.push(newAgency);
    } else if (this.modalMode === 'edit' && this.formData.id) {
      const index = this.agencies.findIndex(a => a.id === this.formData.id);
      if (index !== -1) {
        this.agencies[index] = { ...this.agencies[index], ...this.formData };
      }
    }
    
    this.calculateStats();
    this.filterAgencies();
    this.closeModal();
  }

  deleteAgency(id: string): void {
    if (confirm('Are you sure you want to delete this agency?')) {
      this.agencies = this.agencies.filter(a => a.id !== id);
      this.calculateStats();
      this.filterAgencies();
      
      if (this.selectedAgency?.id === id) {
        this.selectedAgency = null;
      }
    }
  }

  viewAgencyDetails(agency: Agency): void {
    this.selectedAgency = agency;
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  getStatusColor(status: string): string {
    return status === 'active' ? '#10B981' : '#EF4444';
  }

  getStatusBgColor(status: string): string {
    return status === 'active' ? '#D1FAE5' : '#FEE2E2';
  }

  // Add this missing method
  getCountryDistribution(): { name: string; count: number; percentage: number }[] {
    const countryMap = new Map<string, number>();
    this.agencies.forEach(agency => {
      countryMap.set(agency.country, (countryMap.get(agency.country) || 0) + 1);
    });
    
    const total = this.agencies.length;
    return Array.from(countryMap.entries())
      .map(([name, count]) => ({
        name,
        count,
        percentage: (count / total) * 100
      }))
      .sort((a, b) => b.count - a.count);
  }
}