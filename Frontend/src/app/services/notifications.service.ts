import {Injectable, NgZone} from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private client!: Client;
  private notificationsSubject = new BehaviorSubject<any[]>([]);

  // Salvăm o referință pentru a preveni abonările multiple
  private notifSubscription?: StompSubscription;

  constructor(private zone: NgZone) {}

  connect(userId: string): void {
    // Dacă clientul există și e activ, oprim execuția pentru a nu crea altul
    if (this.client && this.client.active) {
      return;
    }

    this.client = new Client({
      brokerURL: `ws://localhost:8080/ws?userId=${userId}`,
      reconnectDelay: 5000,

      onConnect: () => {
        console.log('WebSocket conectat!');

        if (this.notifSubscription) {
          this.notifSubscription.unsubscribe();
        }

        this.notifSubscription = this.client.subscribe('/user/queue/notifications', (message: IMessage) => {


          this.zone.run(() => {
            const data = JSON.parse(message.body);
            const currentList = this.notificationsSubject.getValue();

            const isDuplicate = currentList.find(n => {
              const hasSameId = n.message?.id && data.message?.id && n.message.id === data.message.id;
              const hasSameContent = JSON.stringify(n.message) === JSON.stringify(data.message);
              return hasSameId || hasSameContent;
            });

            if (!isDuplicate) {
              this.notificationsSubject.next([data, ...currentList]);
            }
          });

        });
      },

      onDisconnect: () => console.log('WebSocket deconectat!'),
      onStompError: (frame) => console.error('Eroare STOMP:', frame)
    });

    this.client.activate();
  }

  disconnect(): void {
    if (this.client) {

      this.client.deactivate();
      this.client = undefined as any;
    }

    if (this.notifSubscription) {
      this.notifSubscription = undefined;
    }

    this.notificationsSubject.next([]);
    console.log('WebSocket deconectat complet!');
  }

  getNotifications(): Observable<any[]> {
    return this.notificationsSubject.asObservable();
  }

  clearNotifications(): void {
    this.notificationsSubject.next([]);
  }
}
