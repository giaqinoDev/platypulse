import { PolarAngleAxis, RadialBar, RadialBarChart } from "recharts";
import "../ReportVisuals/HealthScoreRadialChart.css"

interface HealthScoreRadialChartProps{
    input: number|undefined;
}
function HealthScoreRadialChart({input}: HealthScoreRadialChartProps){
    const chartData = [
        {
            name: "Health Score",
            value: input,
            fill: "#ff6139"//"rgba(29, 255, 48, 0.8)"
        }
    ]
    return(
        <div className="radial-health-chart-container">
            <RadialBarChart
                width={180}
                height={180}
                data={chartData}
                startAngle={90}
                endAngle={-270}
                innerRadius="75%"
                outerRadius="100%"
                className="radial-bar-chart"
            >   
                <PolarAngleAxis
                    type="number"
                    domain={[0, 100]}
                    tick={false}

                />
                <RadialBar background={{fill : "#e9f2ff"}} dataKey="value" className="radial-bar"/>
            </RadialBarChart>
            <h1 className="radial-health-score-label">
                {input != undefined && Math.floor(input)}
            </h1>
        </div>

    );
}

export default HealthScoreRadialChart;