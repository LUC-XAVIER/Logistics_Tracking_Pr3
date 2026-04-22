import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/admin/dashboard', pathMatch: 'full' },
  { 
    path: 'admin', 
    loadChildren: () => import('./features/admin/admin-module').then(m => m.AdminModule)
  },
  { path: '**', redirectTo: '/admin/dashboard' }
];