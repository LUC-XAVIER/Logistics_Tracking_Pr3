import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateParcel } from './create-parcel';

describe('CreateParcel', () => {
  let component: CreateParcel;
  let fixture: ComponentFixture<CreateParcel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateParcel],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateParcel);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
