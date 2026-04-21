import { TestBed } from '@angular/core/testing';

import { Parcel } from './parcel';

describe('Parcel', () => {
  let service: Parcel;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Parcel);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
