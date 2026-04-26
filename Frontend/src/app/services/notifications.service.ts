import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private client!: Client;

  private notificationsSubject = new BehaviorSubject<any[]>([]);

  connect(userId: string): void {

    if (this.client) {
      return;
    }

    this.client = new Client({
      brokerURL: `ws://localhost:8080/ws?userId=${userId}`,

      onConnect: () => {
        console.log('WebSocket conectat!');
        this.client.subscribe('/user/queue/notifications', (message: IMessage) => {
          const data = JSON.parse(message.body);
          const currentList = this.notificationsSubject.getValue();

          const isDuplicate = currentList.find(n =>
            (n.id && n.id === data.id) ||
            (n.timestamp === data.timestamp && n.message?.title === data.message?.title)
          );

          if (!isDuplicate) {
            this.notificationsSubject.next([data, ...currentList]);
          }
        });
      },

      onDisconnect: () => console.log('WebSocket deconectat!'),
      onStompError: (frame) => console.error('Eroare STOMP:', frame),
      reconnectDelay: 5000,
    });

    this.client.activate();
  }

  disconnect(): void {
    if (this.client) {
      this.client = undefined as any;
    }
    this.notificationsSubject.next([]);
  }

  getNotifications(): Observable<any[]> {
    return this.notificationsSubject.asObservable();
  }
  clearNotifications(): void {
    this.notificationsSubject.next([]);
  }
}
