import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import type { DownloadDP } from "../../NPMFetch/DownloadDP";
import formatNumber from "../NumFormatter";


interface DownloadsChartProps {
  data: DownloadDP[]|null|undefined;
}

export default function DownloadsChart({data}: DownloadsChartProps) {
    const chartData = data?.filter((item) => item.volume !== null && item.end_date !== null).map((item) => ({
      date: item.end_date,
      volume: item.volume,
    }));

  return (
    <div>
        <p style={{"color": "#212a3b", "fontSize": "larger"}}>Weekly Downloads</p>
    
        <ResponsiveContainer width="100%" height={300}>
        <LineChart
            data={chartData}
            margin={{
            top: 10,
            right: 20,
            left: 0,
            bottom: 10,
            }}
        >
            <CartesianGrid strokeDasharray="3 3" />

            <XAxis
                dataKey="date"
                interval="preserveStartEnd"
                tickFormatter={(date) => {
                    const parsedDate = new Date(date);

                    return parsedDate.toLocaleDateString("en-US", {
                    month: "short",
                    day: "numeric",
                    });
                }}
                stroke="#646d7d"
            />

            <YAxis 
                tickFormatter={(volume: number) => formatNumber(volume) ?? "N/A"}
                stroke="#646d7d"
            />

            <Tooltip
                labelFormatter={(date) => {
                    if (date == null) return "";

                    const parsedDate = new Date(String(date));

                    return parsedDate.toLocaleDateString("en-US", {
                        month: "short",
                        day: "numeric",
                        year: "numeric",
                    });
                }}
                formatter={(value) => [
                    Number(value).toLocaleString(),
                    "Downloads",
                ]}
            />

            <Line
            type="monotone"
            dataKey="volume"
            stroke="#14b8a6"
            strokeWidth={2}
            dot={false}
            activeDot={{ r: 5 }}
            />
        </LineChart>
        </ResponsiveContainer>
    </div>
  );
}