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
  selector: 'app-sidebar-agent',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar-agent.html',
  styleUrls: ['./sidebar-agent.css']
})
export class SidebarAgentComponent implements OnInit {
  navItems: NavItem[] = [];

  constructor(private sanitizer: DomSanitizer, private router: Router) {
    this.initializeNavItems();
  }

  initializeNavItems(): void {
    this.navItems = [
      {
        id: 'dashboard',
        label: 'Dashboard',
        path: '/agent/dashboard',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="3" width="7" height="7" rx="1" />
          <rect x="14" y="3" width="7" height="7" rx="1" />
          <rect x="3" y="14" width="7" height="7" rx="1" />
          <rect x="14" y="14" width="7" height="7" rx="1" />
        </svg>`)
      },
      {
        id: 'my-deliveries',
        label: 'My Deliveries',
        path: '/agent/my-deliveries',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M1 3h15v13H1zM16 8h4l3 3v5h-7V8z" />
          <circle cx="5.5" cy="18.5" r="2.5" />
          <circle cx="18.5" cy="18.5" r="2.5" />
        </svg>`)
      },
      {
        id: 'live-tracking',
        label: 'Live Tracking',
        path: '/agent/live-tracking',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10" />
          <polyline points="12 6 12 12 16 14" />
        </svg>`)
      },
      {
        id: 'delivery-history',
        label: 'Delivery History',
        path: '/agent/delivery-history',
        icon: this.sanitizer.bypassSecurityTrustHtml(`<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
          <polyline points="14 2 14 8 20 8" />
          <line x1="16" y1="13" x2="8" y2="13" />
          <line x1="16" y1="17" x2="8" y2="17" />
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