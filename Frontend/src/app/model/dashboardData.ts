export interface DashboardData {
    totalTemplates: number;
    totalGeneratedDocuments: number;
    pendingApprovals: number;
    receivedApprovals: number;
}

export interface Notification {
    id: number;
    title: string;
    message: string;
    timestamp: string;
    read: boolean;
}