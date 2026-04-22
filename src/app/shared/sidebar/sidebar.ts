import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

export interface NavItem {
  id: string;
  label: string;
  path: string;
  icon: SafeHtml;
  active?: boolean;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.css']
})
export class SidebarComponent implements OnInit {
  navItems: NavItem[] = [];

  constructor(private sanitizer: DomSanitizer, private router: Router) {
    this.initializeNavItems();
  }

  initializeNavItems(): void {
    this.navItems = [
      {
        id: 'dashboard',
        label: 'Dashboard',
        path: '/admin/dashboard',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="3" width="7" height="7" rx="1" />
          <rect x="14" y="3" width="7" height="7" rx="1" />
          <rect x="3" y="14" width="7" height="7" rx="1" />
          <rect x="14" y="14" width="7" height="7" rx="1" />
        </svg>`)
      },
      {
        id: 'agencies',
        label: 'Agencies',
        path: '/admin/agencies',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="2" y="7" width="20" height="14" rx="2" />
          <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
        </svg>`)
      },
      {
        id: 'parcels',
        label: 'Parcels',
        path: '/admin/parcels',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M1 3h15v13H1zM16 8h4l3 3v5h-7V8z" />
          <circle cx="5.5" cy="18.5" r="2.5" />
          <circle cx="18.5" cy="18.5" r="2.5" />
        </svg>`)
      },
      {
        id: 'routes-cache',
        label: 'Route Cache',
        path: '/admin/routes-cache',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="2" y="7" width="13" height="10" rx="1" />
          <path d="M15 9h3l3 3v5h-6V9z" />
          <circle cx="6" cy="19" r="2" />
          <circle cx="18" cy="19" r="2" />
        </svg>`)
      },
      {
        id: 'api-usage',
        label: 'API Usage',
        path: '/admin/api-usage',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10" />
          <path d="M12 2v4M22 12h-4M12 20v-4M4 12H2" />
        </svg>`)
      },
      {
        id: 'users',
        label: 'Users',
        path: '/admin/users',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="8" r="4" />
          <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" />
        </svg>`)
      }
    ];
  }

  ngOnInit(): void {
    this.setActiveNavItem();
    this.router.events.subscribe(() => {
      this.setActiveNavItem();
    });
  }

  setActiveNavItem(): void {
    const currentPath = this.router.url;
    this.navItems.forEach(item => {
      item.active = currentPath === item.path;
    });
  }

  logout(): void {
    console.log('Logging out...');
    this.router.navigate(['/login']);
  }
}