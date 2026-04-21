import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoutesCache } from './routes-cache';

describe('RoutesCache', () => {
  let component: RoutesCache;
  let fixture: ComponentFixture<RoutesCache>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoutesCache],
    }).compileComponents();

    fixture = TestBed.createComponent(RoutesCache);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
