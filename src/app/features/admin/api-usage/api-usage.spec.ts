import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiUsage } from './api-usage';

describe('ApiUsage', () => {
  let component: ApiUsage;
  let fixture: ComponentFixture<ApiUsage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ApiUsage],
    }).compileComponents();

    fixture = TestBed.createComponent(ApiUsage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
