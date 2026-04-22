import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
  standalone: true,
  templateUrl: './agencies.html',
  styleUrls: ['./agencies.css'],
  imports: [CommonModule, FormsModule, SidebarComponent]
})
export class AdminAgenciesComponent implements OnInit, AfterViewInit {
  private map: any;
  private markers: any[] = [];
  private mapInitialized: boolean = false;

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

  ngAfterViewInit(): void {
    // Don't initialize map here since container might not be visible
    // Map will be initialized when tab is clicked
  }

  private initMap(): void {
    if (this.mapInitialized) return;
    
    const mapElement = document.getElementById('agencyMap');
    if (!mapElement) return;

    // Initialize map centered on Germany
    this.map = L.map('agencyMap').setView([51.1657, 10.4515], 6);

    // Add OpenStreetMap tiles
    L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> &copy; <a href="https://carto.com/attributions">CARTO</a>',
      subdomains: 'abcd',
      maxZoom: 19,
      minZoom: 3
    }).addTo(this.map);

    this.mapInitialized = true;
    this.addMarkersToMap();
  }

  private addMarkersToMap(): void {
    if (!this.map) return;
    
    // Clear existing markers
    this.markers.forEach(marker => this.map.removeLayer(marker));
    this.markers = [];

    // Add markers for each agency
    this.agencies.forEach(agency => {
      const markerColor = agency.status === 'active' ? '#10B981' : '#EF4444';
      
      const customIcon = L.divIcon({
        className: 'custom-marker',
        html: `<div style="background-color: ${markerColor}; width: 12px; height: 12px; border-radius: 50%; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.2);"></div>`,
        iconSize: [12, 12],
        popupAnchor: [0, -6]
      });

      const marker = L.marker([agency.latitude, agency.longitude], { icon: customIcon })
        .bindPopup(`
          <div style="font-family: 'DM Sans', sans-serif; min-width: 200px;">
            <strong style="color: ${markerColor};">🏢 ${agency.name}</strong><br/>
            📍 ${agency.address}<br/>
            📞 ${agency.phone}<br/>
            ✉️ ${agency.email}<br/>
            <span style="font-size: 11px; color: #6B7280;">Status: ${agency.status.toUpperCase()} | Parcels: ${agency.totalParcels || 0}</span>
          </div>
        `)
        .addTo(this.map);
      
      this.markers.push(marker);
    });

    // Fit map to show all markers
    if (this.agencies.length > 0 && this.map) {
      const bounds = L.latLngBounds(this.agencies.map(a => [a.latitude, a.longitude]));
      this.map.fitBounds(bounds, { padding: [50, 50] });
    }
  }

  updateMapFilters(): void {
    if (!this.map) return;
    
    // Filter markers based on current filters
    const filteredAgencies = this.agencies.filter(agency => {
      const matchesSearch = this.searchTerm === '' || 
        agency.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        agency.town.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesCountry = this.selectedCountry === '' || agency.country === this.selectedCountry;
      const matchesStatus = this.selectedStatus === '' || agency.status === this.selectedStatus;
      
      return matchesSearch && matchesCountry && matchesStatus;
    });

    // Update markers visibility
    this.markers.forEach((marker, index) => {
      const agency = this.agencies[index];
      const isVisible = filteredAgencies.some(f => f.id === agency.id);
      if (isVisible) {
        this.map.addLayer(marker);
      } else {
        this.map.removeLayer(marker);
      }
    });

    // Fit bounds to visible markers
    if (filteredAgencies.length > 0 && this.map) {
      const bounds = L.latLngBounds(filteredAgencies.map(a => [a.latitude, a.longitude]));
      this.map.fitBounds(bounds, { padding: [50, 50] });
    }
  }

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
    
    // Update map when filters change
    if (this.activeTab === 'map' && this.map) {
      this.updateMapFilters();
    }
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
    if (this.mapInitialized) {
      this.addMarkersToMap();
    }
    this.closeModal();
  }

  deleteAgency(id: string): void {
    if (confirm('Are you sure you want to delete this agency?')) {
      this.agencies = this.agencies.filter(a => a.id !== id);
      this.calculateStats();
      this.filterAgencies();
      if (this.mapInitialized) {
        this.addMarkersToMap();
      }
      
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
    if (tab === 'map') {
      setTimeout(() => {
        if (!this.mapInitialized) {
          this.initMap();
        } else if (this.map) {
          this.map.invalidateSize();
          this.updateMapFilters();
        }
      }, 100);
    }
  }

  getStatusColor(status: string): string {
    return status === 'active' ? '#10B981' : '#EF4444';
  }

  getStatusBgColor(status: string): string {
    return status === 'active' ? '#D1FAE5' : '#FEE2E2';
  }

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

  // Map controls
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
    if (this.map && this.agencies.length > 0) {
      const bounds = L.latLngBounds(this.agencies.map(a => [a.latitude, a.longitude]));
      this.map.fitBounds(bounds, { padding: [50, 50] });
    }
  }
}