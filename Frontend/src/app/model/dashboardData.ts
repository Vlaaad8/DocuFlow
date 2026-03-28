export interface DashboardData {
    totalTemplates: number;
    totalGeneratedDocuments: number;
    pendingApprovals: number;
    receivedApprovals: number;
    chartData: {title: string, timestamp: string, status: string}[];
    sourceDistribution: {source: string, value: number}[];
}

export interface Notification {
    id: number;
    title: string;
    message: string;
    timestamp: string;
    read: boolean;
}
