import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminRoutingModule } from './admin-routing-module';

// Import all admin components
import { AdminDashboardComponent } from './dashboard/dashboard';
import { AdminAgenciesComponent } from './agencies/agencies';
import { AdminParcelsComponent } from './parcels/parcels';
import { AdminRoutesCacheComponent } from './routes-cache/routes-cache';
import { AdminApiUsageComponent } from './api-usage/api-usage';
import { AdminUsersComponent } from './users/users';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    AdminRoutingModule, 
    AdminDashboardComponent,
    AdminAgenciesComponent,
    AdminParcelsComponent,
    AdminRoutesCacheComponent,
    AdminApiUsageComponent,
    AdminUsersComponent
  ],
  exports: [
    AdminDashboardComponent,
    AdminAgenciesComponent,
    AdminParcelsComponent,
    AdminRoutesCacheComponent,
    AdminApiUsageComponent,
    AdminUsersComponent
  ]
})
export class AdminModule { }