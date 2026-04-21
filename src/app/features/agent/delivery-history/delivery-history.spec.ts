import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliveryHistory } from './delivery-history';

describe('DeliveryHistory', () => {
  let component: DeliveryHistory;
  let fixture: ComponentFixture<DeliveryHistory>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeliveryHistory],
    }).compileComponents();

    fixture = TestBed.createComponent(DeliveryHistory);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
