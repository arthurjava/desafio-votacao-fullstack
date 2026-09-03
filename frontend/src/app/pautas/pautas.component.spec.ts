import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PautasComponent } from './pautas.component';

describe('PautasComponent', () => {
  let component: PautasComponent;
  let fixture: ComponentFixture<PautasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PautasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PautasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
