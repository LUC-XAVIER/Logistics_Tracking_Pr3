import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgentRoutingModule } from './agent-routing-module';

// Import all agent components
import { AgentDashboardComponent } from './dashboard/dashboard';
import { MyDeliveriesComponent } from './my-deliveries/my-deliveries';
import { LiveTrackingComponent } from './live-tracking/live-tracking';
import { DeliveryHistoryComponent } from './delivery-history/delivery-history';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    AgentRoutingModule, 
    AgentDashboardComponent,
    MyDeliveriesComponent,
    LiveTrackingComponent,
    DeliveryHistoryComponent
  ],
  exports: [
    AgentDashboardComponent,
    MyDeliveriesComponent,
    LiveTrackingComponent,
    DeliveryHistoryComponent
  ]
})
export class AgentModule {}