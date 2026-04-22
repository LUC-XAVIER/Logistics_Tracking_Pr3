import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { SidebarComponent } from '../../../shared/sidebar/sidebar';

interface ApiEndpoint {
  id: string;
  name: string;
  description: string;
  callsToday: number;
  callsTotal: number;
  dailyLimit: number;
  avgResponseTime: number;
  successRate: number;
  lastUsed: Date;
  status: 'healthy' | 'warning' | 'error';
}

interface ApiCallLog {
  id: string;
  endpoint: string;
  timestamp: Date;
  responseTime: number;
  statusCode: number;
  success: boolean;
  cached: boolean;
  ipAddress: string;
}

interface DailyStats {
  date: Date;
  geocodingCalls: number;
  routingCalls: number;
  cacheHits: number;
  cacheMisses: number;
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

@Component({
  selector: 'app-admin-api-usage',
  standalone: true,
  templateUrl: './api-usage.html',
  styleUrls: ['./api-usage.css'],
  imports: [CommonModule, FormsModule, SidebarComponent]
})
export class AdminApiUsageComponent implements OnInit {
  Math = Math;

  // API Endpoints data
  apiEndpoints: ApiEndpoint[] = [
    {
      id: '1',
      name: 'Geocoding API',
      description: 'Convert addresses to coordinates',
      callsToday: 187,
      callsTotal: 12450,
      dailyLimit: 2500,
      avgResponseTime: 245,
      successRate: 99.2,
      lastUsed: new Date(),
      status: 'healthy'
    },
    {
      id: '2',
      name: 'Routing API',
      description: 'Calculate routes and distances',
      callsToday: 94,
      callsTotal: 8750,
      dailyLimit: 2500,
      avgResponseTime: 412,
      successRate: 98.7,
      lastUsed: new Date(),
      status: 'healthy'
    },
    {
      id: '3',
      name: 'Directions API',
      description: 'Get turn-by-turn directions',
      callsToday: 156,
      callsTotal: 11200,
      dailyLimit: 2500,
      avgResponseTime: 389,
      successRate: 97.5,
      lastUsed: new Date(),
      status: 'healthy'
    },
    {
      id: '4',
      name: 'Distance Matrix API',
      description: 'Calculate travel distance and time',
      callsToday: 45,
      callsTotal: 3200,
      dailyLimit: 1000,
      avgResponseTime: 567,
      successRate: 96.8,
      lastUsed: new Date(),
      status: 'warning'
    }
  ];

  // Recent API call logs
  apiLogs: ApiCallLog[] = [
    {
      id: '1',
      endpoint: 'Geocoding API',
      timestamp: new Date('2025-01-21T10:23:00'),
      responseTime: 234,
      statusCode: 200,
      success: true,
      cached: false,
      ipAddress: '192.168.1.1'
    },
    {
      id: '2',
      endpoint: 'Routing API',
      timestamp: new Date('2025-01-21T10:15:00'),
      responseTime: 401,
      statusCode: 200,
      success: true,
      cached: true,
      ipAddress: '192.168.1.1'
    },
    {
      id: '3',
      endpoint: 'Geocoding API',
      timestamp: new Date('2025-01-21T09:45:00'),
      responseTime: 567,
      statusCode: 200,
      success: true,
      cached: false,
      ipAddress: '192.168.1.2'
    },
    {
      id: '4',
      endpoint: 'Directions API',
      timestamp: new Date('2025-01-21T09:30:00'),
      responseTime: 389,
      statusCode: 200,
      success: true,
      cached: true,
      ipAddress: '192.168.1.1'
    },
    {
      id: '5',
      endpoint: 'Distance Matrix API',
      timestamp: new Date('2025-01-21T09:12:00'),
      responseTime: 234,
      statusCode: 429,
      success: false,
      cached: false,
      ipAddress: '192.168.1.3'
    }
  ];

  // Daily statistics for charts
  dailyStats: DailyStats[] = [
    { date: new Date('2025-01-15'), geocodingCalls: 210, routingCalls: 120, cacheHits: 180, cacheMisses: 150 },
    { date: new Date('2025-01-16'), geocodingCalls: 195, routingCalls: 135, cacheHits: 200, cacheMisses: 130 },
    { date: new Date('2025-01-17'), geocodingCalls: 220, routingCalls: 140, cacheHits: 210, cacheMisses: 150 },
    { date: new Date('2025-01-18'), geocodingCalls: 205, routingCalls: 125, cacheHits: 195, cacheMisses: 135 },
    { date: new Date('2025-01-19'), geocodingCalls: 230, routingCalls: 150, cacheHits: 220, cacheMisses: 160 },
    { date: new Date('2025-01-20'), geocodingCalls: 215, routingCalls: 130, cacheHits: 205, cacheMisses: 140 },
    { date: new Date('2025-01-21'), geocodingCalls: 187, routingCalls: 94, cacheHits: 178, cacheMisses: 103 }
  ];

  filteredLogs: ApiCallLog[] = [];
  selectedEndpoint: string = '';
  selectedStatus: string = '';
  searchTerm: string = '';
  activeView: string = 'overview';
  showDetailsModal: boolean = false;
  selectedLog: ApiCallLog | null = null;

  // Stats cards
  statsCards: StatCard[] = [];

  // Rate limit alerts
  rateLimitAlerts = [
    { endpoint: 'Geocoding API', usage: 187, limit: 2500, percentage: 7.5 },
    { endpoint: 'Routing API', usage: 94, limit: 2500, percentage: 3.8 },
    { endpoint: 'Directions API', usage: 156, limit: 2500, percentage: 6.2 },
    { endpoint: 'Distance Matrix API', usage: 45, limit: 1000, percentage: 4.5 }
  ];

  constructor(private sanitizer: DomSanitizer) {
    this.filteredLogs = [...this.apiLogs];
    this.calculateStats();
  }

  ngOnInit(): void {}

  calculateStats(): void {
    const totalCallsToday = this.apiEndpoints.reduce((sum, e) => sum + e.callsToday, 0);
    const totalCallsAll = this.apiEndpoints.reduce((sum, e) => sum + e.callsTotal, 0);
    const avgResponseTime = Math.round(this.apiEndpoints.reduce((sum, e) => sum + e.avgResponseTime, 0) / this.apiEndpoints.length);
    const cacheHitRate = this.dailyStats[this.dailyStats.length - 1].cacheHits / 
                         (this.dailyStats[this.dailyStats.length - 1].cacheHits + this.dailyStats[this.dailyStats.length - 1].cacheMisses) * 100;

    this.statsCards = [
      {
        label: 'Total API Calls Today',
        value: totalCallsToday,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
          <path d="M20 12a8 8 0 0 0-16 0M4 12h16M12 2v20M8 7l-3-3M16 7l3-3"/>
        </svg>`),
        color: '#3B82F6',
        bgColor: '#EFF6FF',
        trend: 12,
        unit: 'calls'
      },
      {
        label: 'Total API Calls (All Time)',
        value: totalCallsAll,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
          <path d="M21 12v3a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4v-3M12 2v12M12 14l3-3M12 14l-3-3"/>
        </svg>`),
        color: '#10B981',
        bgColor: '#D1FAE5',
        trend: 8,
        unit: 'calls'
      },
      {
        label: 'Avg Response Time',
        value: avgResponseTime,
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
          <path d="M13 2L3 14h8l-2 8 10-12h-8l2-8z"/>
        </svg>`),
        color: '#F59E0B',
        bgColor: '#FEF3C7',
        trend: -5,
        unit: 'ms'
      },
      {
        label: 'Cache Hit Rate',
        value: Math.round(cacheHitRate),
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="2">
          <ellipse cx="12" cy="5" rx="9" ry="3"/>
          <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/>
          <path d="M3 12c0 1.66 4 3 9 3s9-1.34 9-3"/>
        </svg>`),
        color: '#8B5CF6',
        bgColor: '#F3E8FF',
        trend: 15,
        unit: '%'
      }
    ];
  }

  getEndpointIcon(endpoint: string): SafeHtml {
    const icons: any = {
      'Geocoding API': this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#3B82F6" stroke-width="2">
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
        <circle cx="12" cy="10" r="3"/>
      </svg>`),
      'Routing API': this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#10B981" stroke-width="2">
        <path d="M2 12h7M15 12h7M12 2v7M12 15v7"/>
        <circle cx="12" cy="12" r="3"/>
      </svg>`),
      'Directions API': this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#F59E0B" stroke-width="2">
        <path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83"/>
        <path d="M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/>
        <circle cx="12" cy="12" r="4"/>
      </svg>`),
      'Distance Matrix API': this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="2">
        <rect x="3" y="3" width="18" height="18" rx="2"/>
        <path d="M3 9h18M3 15h18M9 3v18M15 3v18"/>
      </svg>`)
    };
    return icons[endpoint] || icons['Geocoding API'];
  }

  filterLogs(): void {
    this.filteredLogs = this.apiLogs.filter(log => {
      const matchesEndpoint = this.selectedEndpoint === '' || log.endpoint === this.selectedEndpoint;
      const matchesStatus = this.selectedStatus === '' || 
        (this.selectedStatus === 'success' && log.success) ||
        (this.selectedStatus === 'failed' && !log.success);
      const matchesSearch = this.searchTerm === '' || 
        log.endpoint.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        log.ipAddress.includes(this.searchTerm);
      
      return matchesEndpoint && matchesStatus && matchesSearch;
    });
  }

  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.filterLogs();
  }

  onEndpointFilter(event: Event): void {
    this.selectedEndpoint = (event.target as HTMLSelectElement).value;
    this.filterLogs();
  }

  onStatusFilter(event: Event): void {
    this.selectedStatus = (event.target as HTMLSelectElement).value;
    this.filterLogs();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedEndpoint = '';
    this.selectedStatus = '';
    this.filterLogs();
  }

  setActiveView(view: string): void {
    this.activeView = view;
  }

  viewLogDetails(log: ApiCallLog): void {
    this.selectedLog = log;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedLog = null;
  }

  getStatusColor(status: string): string {
    switch(status) {
      case 'healthy': return '#10B981';
      case 'warning': return '#F59E0B';
      case 'error': return '#EF4444';
      default: return '#6B7280';
    }
  }

  getStatusBgColor(status: string): string {
    switch(status) {
      case 'healthy': return '#D1FAE5';
      case 'warning': return '#FEF3C7';
      case 'error': return '#FEE2E2';
      default: return '#F3F4F6';
    }
  }

  getChartMaxValue(): number {
    const maxGeocoding = Math.max(...this.dailyStats.map(d => d.geocodingCalls));
    const maxRouting = Math.max(...this.dailyStats.map(d => d.routingCalls));
    return Math.max(maxGeocoding, maxRouting) + 50;
  }
}