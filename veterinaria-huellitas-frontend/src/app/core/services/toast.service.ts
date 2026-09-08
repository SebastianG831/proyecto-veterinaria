import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ToastService {
  message = signal<string>('');
  visible = signal<boolean>(false);

  private timer: ReturnType<typeof setTimeout> | undefined;

  show(msg: string): void {
    this.message.set(msg);
    this.visible.set(true);
    clearTimeout(this.timer);
    this.timer = setTimeout(() => this.visible.set(false), 2600);
  }
}
