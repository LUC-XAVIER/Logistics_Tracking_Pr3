import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AgentDashboardComponent } from './dashboard/dashboard';
import { MyDeliveriesComponent } from './my-deliveries/my-deliveries';
import { LiveTrackingComponent } from './live-tracking/live-tracking';
import { DeliveryHistoryComponent } from './delivery-history/delivery-history';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    component: AgentDashboardComponent,
    data: { title: 'Agent Dashboard' }
  },
  {
    path: 'my-deliveries',
    component: MyDeliveriesComponent,
    data: { title: 'My Deliveries' }
  },
  {
    path: 'live-tracking',
    component: LiveTrackingComponent,
    data: { title: 'Live Tracking' }
  },
  {
    path: 'delivery-history',
    component: DeliveryHistoryComponent,
    data: { title: 'Delivery History' }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AgentRoutingModule {}