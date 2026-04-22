import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SidebarComponent } from '../../../shared/sidebar/sidebar';

interface CachedRoute {
  id: string;
  origin: string;
  destination: string;
  originCoordinates: { lat: number; lng: number };
  destinationCoordinates: { lat: number; lng: number };
  distance: number;
  duration: number;
  segments: RouteSegment[];
  createdAt: Date;
  lastUsed: Date;
  usageCount: number;
  estimatedCost: number;
  roadTypes: {
    town: number;
    highway: number;
    motorway: number;
  };
}

interface RouteSegment {
  type: 'town' | 'highway' | 'motorway';
  distance: number;
  duration: number;
  startPoint: string;
  endPoint: string;
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
  selector: 'app-admin-routes-cache',
  standalone: true,
  templateUrl: './routes-cache.html',
  styleUrls: ['./routes-cache.css'],
  imports: [CommonModule, FormsModule, SidebarComponent]
})
export class AdminRoutesCacheComponent implements OnInit {
  Math = Math;

  // Sample cached routes data
  cachedRoutes: CachedRoute[] = [
    {
      id: '1',
      origin: 'Berlin Central Hub',
      destination: 'Munich Logistics Center',
      originCoordinates: { lat: 52.5123, lng: 13.3889 },
      destinationCoordinates: { lat: 48.1351, lng: 11.5820 },
      distance: 504,
      duration: 330,
      segments: [
        { type: 'town', distance: 5, duration: 15, startPoint: 'Berlin Hub', endPoint: 'A100' },
        { type: 'motorway', distance: 480, duration: 270, startPoint: 'A100', endPoint: 'A9 Munich' },
        { type: 'town', distance: 19, duration: 45, startPoint: 'A9 Munich', endPoint: 'Munich Center' }
      ],
      createdAt: new Date('2025-01-15'),
      lastUsed: new Date('2025-01-21'),
      usageCount: 45,
      estimatedCost: 345.50,
      roadTypes: { town: 24, highway: 0, motorway: 480 }
    },
    {
      id: '2',
      origin: 'Hamburg Port Agency',
      destination: 'Cologne Rhein Hub',
      originCoordinates: { lat: 53.5511, lng: 9.9937 },
      destinationCoordinates: { lat: 50.9375, lng: 6.9603 },
      distance: 412,
      duration: 285,
      segments: [
        { type: 'town', distance: 8, duration: 20, startPoint: 'Hamburg Port', endPoint: 'A7' },
        { type: 'highway', distance: 350, duration: 210, startPoint: 'A7', endPoint: 'A1' },
        { type: 'town', distance: 54, duration: 55, startPoint: 'A1', endPoint: 'Cologne Hub' }
      ],
      createdAt: new Date('2025-01-10'),
      lastUsed: new Date('2025-01-20'),
      usageCount: 32,
      estimatedCost: 287.50,
      roadTypes: { town: 62, highway: 350, motorway: 0 }
    },
    {
      id: '3',
      origin: 'Frankfurt Airport Logistics',
      destination: 'Berlin Central Hub',
      originCoordinates: { lat: 50.1109, lng: 8.6821 },
      destinationCoordinates: { lat: 52.5123, lng: 13.3889 },
      distance: 547,
      duration: 360,
      segments: [
        { type: 'town', distance: 10, duration: 25, startPoint: 'Frankfurt Airport', endPoint: 'A5' },
        { type: 'motorway', distance: 520, duration: 300, startPoint: 'A5', endPoint: 'A10 Berlin' },
        { type: 'town', distance: 17, duration: 35, startPoint: 'A10 Berlin', endPoint: 'Berlin Hub' }
      ],
      createdAt: new Date('2025-01-05'),
      lastUsed: new Date('2025-01-19'),
      usageCount: 67,
      estimatedCost: 398.00,
      roadTypes: { town: 27, highway: 0, motorway: 520 }
    },
    {
      id: '4',
      origin: 'Cologne Rhein Hub',
      destination: 'Hamburg Port Agency',
      originCoordinates: { lat: 50.9375, lng: 6.9603 },
      destinationCoordinates: { lat: 53.5511, lng: 9.9937 },
      distance: 412,
      duration: 290,
      segments: [
        { type: 'town', distance: 6, duration: 15, startPoint: 'Cologne Hub', endPoint: 'A1' },
        { type: 'highway', distance: 390, duration: 240, startPoint: 'A1', endPoint: 'A7 Hamburg' },
        { type: 'town', distance: 16, duration: 35, startPoint: 'A7 Hamburg', endPoint: 'Hamburg Port' }
      ],
      createdAt: new Date('2025-01-12'),
      lastUsed: new Date('2025-01-18'),
      usageCount: 28,
      estimatedCost: 287.50,
      roadTypes: { town: 22, highway: 390, motorway: 0 }
    },
    {
      id: '5',
      origin: 'Munich Logistics Center',
      destination: 'Frankfurt Airport Logistics',
      originCoordinates: { lat: 48.1351, lng: 11.5820 },
      destinationCoordinates: { lat: 50.1109, lng: 8.6821 },
      distance: 392,
      duration: 260,
      segments: [
        { type: 'town', distance: 12, duration: 30, startPoint: 'Munich Center', endPoint: 'A9' },
        { type: 'motorway', distance: 360, duration: 200, startPoint: 'A9', endPoint: 'A5 Frankfurt' },
        { type: 'town', distance: 20, duration: 30, startPoint: 'A5 Frankfurt', endPoint: 'Frankfurt Airport' }
      ],
      createdAt: new Date('2025-01-08'),
      lastUsed: new Date('2025-01-17'),
      usageCount: 39,
      estimatedCost: 275.00,
      roadTypes: { town: 32, highway: 0, motorway: 360 }
    },
    {
      id: '6',
      origin: 'Berlin Central Hub',
      destination: 'Hamburg Port Agency',
      originCoordinates: { lat: 52.5123, lng: 13.3889 },
      destinationCoordinates: { lat: 53.5511, lng: 9.9937 },
      distance: 289,
      duration: 195,
      segments: [
        { type: 'town', distance: 7, duration: 20, startPoint: 'Berlin Hub', endPoint: 'A24' },
        { type: 'highway', distance: 260, duration: 150, startPoint: 'A24', endPoint: 'A1 Hamburg' },
        { type: 'town', distance: 22, duration: 25, startPoint: 'A1 Hamburg', endPoint: 'Hamburg Port' }
      ],
      createdAt: new Date('2025-01-03'),
      lastUsed: new Date('2025-01-16'),
      usageCount: 52,
      estimatedCost: 198.50,
      roadTypes: { town: 29, highway: 260, motorway: 0 }
    }
  ];

  filteredRoutes: CachedRoute[] = [];
  selectedRoute: CachedRoute | null = null;
  showDetailsModal: boolean = false;
  showAddModal: boolean = false;
  showClearCacheModal: boolean = false;
  searchTerm: string = '';
  selectedOrigin: string = '';
  selectedDestination: string = '';
  sortBy: string = 'usageCount';
  sortOrder: 'asc' | 'desc' = 'desc';
  activeView: string = 'list';

  // Form for adding new route
  newRoute: any = {
    origin: '',
    destination: '',
    distance: 0,
    duration: 0
  };

  // Unique origins and destinations for filters
  origins: string[] = [];
  destinations: string[] = [];

  // Stats
  statsCards: StatCard[] = [];

  // Cache info
  cacheInfo = {
    totalSize: 0,
    totalDistance: 0,
    totalUsage: 0,
    hitRate: 94,
    apisSaved: 0
  };

  constructor(private sanitizer: DomSanitizer) {
    this.filteredRoutes = [...this.cachedRoutes];
    this.calculateStats();
    this.extractUniqueLocations();
    this.calculateCacheInfo();
  }

  ngOnInit(): void {}

  calculateStats(): void {
    const totalDistance = this.cachedRoutes.reduce((sum, r) => sum + r.distance, 0);
    const avgDistance = totalDistance / this.cachedRoutes.length;
    const totalUsage = this.cachedRoutes.reduce((sum, r) => sum + r.usageCount, 0);
    
    this.statsCards = [
      {
        label: 'Total Cached Routes',
        value: this.cachedRoutes.length,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
          <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
          <circle cx="12" cy="10" r="3"/>
        </svg>`),
        color: '#3B82F6',
        bgColor: '#EFF6FF',
        trend: 12
      },
      {
        label: 'Total Distance',
        value: totalDistance,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
          <path d="M2 12h20M12 2v20"/>
        </svg>`),
        color: '#10B981',
        bgColor: '#D1FAE5',
        trend: 8
      },
      {
        label: 'Avg Route Distance',
        value: Math.round(avgDistance),
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
          <rect x="3" y="3" width="18" height="18" rx="2"/>
          <path d="M3 9h18M3 15h18M9 3v18M15 3v18"/>
        </svg>`),
        color: '#F59E0B',
        bgColor: '#FEF3C7',
        trend: -3
      },
      {
        label: 'Total API Calls Saved',
        value: totalUsage,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="2">
          <path d="M20 12H4M12 4v16M8 8l4-4 4 4M8 16l4 4 4-4"/>
        </svg>`),
        color: '#8B5CF6',
        bgColor: '#F3E8FF',
        trend: 23
      }
    ];
  }

  getSegmentIcon(type: string): SafeHtml {
    const icons: any = {
      'town': this.sanitizer.bypassSecurityTrustHtml(`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
        <rect x="2" y="7" width="20" height="14" rx="2"/>
        <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
      </svg>`),
      'highway': this.sanitizer.bypassSecurityTrustHtml(`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
        <path d="M1 3h15v13H1zM16 8h4l3 3v5h-7V8z"/>
        <circle cx="5.5" cy="18.5" r="2.5"/>
        <circle cx="18.5" cy="18.5" r="2.5"/>
      </svg>`),
      'motorway': this.sanitizer.bypassSecurityTrustHtml(`<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
        <rect x="2" y="7" width="13" height="10" rx="1"/>
        <path d="M15 9h3l3 3v5h-6V9z"/>
        <circle cx="6" cy="19" r="2"/>
        <circle cx="18" cy="19" r="2"/>
      </svg>`)
    };
    return icons[type] || icons['town'];
  }

  getPointIcon(type: string): SafeHtml {
    const icons: any = {
      'origin': this.sanitizer.bypassSecurityTrustHtml(`<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
        <circle cx="12" cy="10" r="3"/>
      </svg>`),
      'destination': this.sanitizer.bypassSecurityTrustHtml(`<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#EF4444" stroke-width="2">
        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
      </svg>`)
    };
    return icons[type] || icons['origin'];
  }

  getStatBadgeIcon(type: string): SafeHtml {
    const icons: any = {
      'distance': this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M2 12h20M12 2v20"/>
      </svg>`),
      'duration': this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10"/>
        <polyline points="12 6 12 12 16 14"/>
      </svg>`),
      'usage': this.sanitizer.bypassSecurityTrustHtml(`<svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M20 12H4M12 4v16M8 8l4-4 4 4M8 16l4 4 4-4"/>
      </svg>`)
    };
    return icons[type] || icons['distance'];
  }

  calculateCacheInfo(): void {
    this.cacheInfo.totalSize = this.cachedRoutes.length;
    this.cacheInfo.totalDistance = this.cachedRoutes.reduce((sum, r) => sum + r.distance, 0);
    this.cacheInfo.totalUsage = this.cachedRoutes.reduce((sum, r) => sum + r.usageCount, 0);
    this.cacheInfo.apisSaved = this.cacheInfo.totalUsage;
  }

  extractUniqueLocations(): void {
    const originsSet = new Set<string>();
    const destinationsSet = new Set<string>();
    
    this.cachedRoutes.forEach(route => {
      originsSet.add(route.origin);
      destinationsSet.add(route.destination);
    });
    
    this.origins = Array.from(originsSet);
    this.destinations = Array.from(destinationsSet);
  }

  filterRoutes(): void {
    this.filteredRoutes = this.cachedRoutes.filter(route => {
      const matchesSearch = this.searchTerm === '' || 
        route.origin.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        route.destination.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesOrigin = this.selectedOrigin === '' || route.origin === this.selectedOrigin;
      const matchesDestination = this.selectedDestination === '' || route.destination === this.selectedDestination;
      
      return matchesSearch && matchesOrigin && matchesDestination;
    });

    this.sortRoutes();
  }

  sortRoutes(): void {
    this.filteredRoutes.sort((a, b) => {
      let aVal: any;
      let bVal: any;
      
      switch(this.sortBy) {
        case 'origin':
          aVal = a.origin;
          bVal = b.origin;
          break;
        case 'destination':
          aVal = a.destination;
          bVal = b.destination;
          break;
        case 'distance':
          aVal = a.distance;
          bVal = b.distance;
          break;
        case 'duration':
          aVal = a.duration;
          bVal = b.duration;
          break;
        case 'usageCount':
          aVal = a.usageCount;
          bVal = b.usageCount;
          break;
        case 'lastUsed':
          aVal = a.lastUsed.getTime();
          bVal = b.lastUsed.getTime();
          break;
        default:
          aVal = a.usageCount;
          bVal = b.usageCount;
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
    this.filterRoutes();
  }

  onOriginFilter(event: Event): void {
    this.selectedOrigin = (event.target as HTMLSelectElement).value;
    this.filterRoutes();
  }

  onDestinationFilter(event: Event): void {
    this.selectedDestination = (event.target as HTMLSelectElement).value;
    this.filterRoutes();
  }

  onSortChange(event: Event): void {
    this.sortBy = (event.target as HTMLSelectElement).value;
    this.sortRoutes();
  }

  toggleSortOrder(): void {
    this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    this.sortRoutes();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedOrigin = '';
    this.selectedDestination = '';
    this.filterRoutes();
  }

  setActiveView(view: string): void {
    this.activeView = view;
  }

  viewRouteDetails(route: CachedRoute): void {
    this.selectedRoute = route;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedRoute = null;
  }

  openAddModal(): void {
    this.newRoute = {
      origin: '',
      destination: '',
      distance: 0,
      duration: 0
    };
    this.showAddModal = true;
  }

  closeAddModal(): void {
    this.showAddModal = false;
  }

  addRoute(): void {
    if (this.newRoute.origin && this.newRoute.destination && this.newRoute.distance > 0) {
      const newRoute: CachedRoute = {
        id: (this.cachedRoutes.length + 1).toString(),
        origin: this.newRoute.origin,
        destination: this.newRoute.destination,
        originCoordinates: { lat: 0, lng: 0 },
        destinationCoordinates: { lat: 0, lng: 0 },
        distance: this.newRoute.distance,
        duration: this.newRoute.duration,
        segments: [],
        createdAt: new Date(),
        lastUsed: new Date(),
        usageCount: 0,
        estimatedCost: this.newRoute.distance * 0.65,
        roadTypes: { town: this.newRoute.distance, highway: 0, motorway: 0 }
      };
      
      this.cachedRoutes.push(newRoute);
      this.calculateStats();
      this.extractUniqueLocations();
      this.filterRoutes();
      this.closeAddModal();
    }
  }

  openClearCacheModal(): void {
    this.showClearCacheModal = true;
  }

  closeClearCacheModal(): void {
    this.showClearCacheModal = false;
  }

  clearCache(): void {
    this.cachedRoutes = [];
    this.filteredRoutes = [];
    this.calculateStats();
    this.extractUniqueLocations();
    this.closeClearCacheModal();
  }

  deleteRoute(id: string): void {
    if (confirm('Are you sure you want to delete this cached route?')) {
      this.cachedRoutes = this.cachedRoutes.filter(r => r.id !== id);
      this.calculateStats();
      this.extractUniqueLocations();
      this.filterRoutes();
      
      if (this.selectedRoute?.id === id) {
        this.closeDetailsModal();
      }
    }
  }

  refreshCache(): void {
    alert('Cache refresh initiated. This will fetch latest routes from API.');
  }

  formatDuration(minutes: number): string {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}m`;
  }

  formatDistance(km: number): string {
    return `${km} km`;
  }

  getSegmentColor(type: string): string {
    switch(type) {
      case 'town': return '#10B981';
      case 'highway': return '#F59E0B';
      case 'motorway': return '#3B82F6';
      default: return '#6B7280';
    }
  }

  getEfficiencyClass(usageCount: number): string {
    if (usageCount > 50) return 'efficiency-high';
    if (usageCount > 20) return 'efficiency-medium';
    return 'efficiency-low';
  }
}