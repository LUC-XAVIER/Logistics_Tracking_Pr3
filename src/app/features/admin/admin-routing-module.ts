import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminDashboardComponent } from './dashboard/dashboard';
import { AdminAgenciesComponent } from './agencies/agencies';
import { AdminParcelsComponent } from './parcels/parcels';
import { AdminRoutesCacheComponent } from './routes-cache/routes-cache';
import { AdminApiUsageComponent } from './api-usage/api-usage';
import { AdminUsersComponent } from './users/users';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    component: AdminDashboardComponent,
    data: { title: 'Admin Dashboard' }
  },
  {
    path: 'agencies',
    component: AdminAgenciesComponent,
    data: { title: 'Manage Agencies' }
  },
  {
    path: 'parcels',
    component: AdminParcelsComponent,
    data: { title: 'Manage Parcels' }
  },
  {
    path: 'routes-cache',
    component: AdminRoutesCacheComponent,
    data: { title: 'Route Cache Management' }
  },
  {
    path: 'api-usage',
    component: AdminApiUsageComponent,
    data: { title: 'API Usage Monitoring' }
  },
  {
    path: 'users',
    component: AdminUsersComponent,
    data: { title: 'User Management' }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AdminRoutingModule { }