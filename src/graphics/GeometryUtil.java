package graphics;

public class GeometryUtil {

	public static double distanceFromPoint(Point p1, Point p2) {
		int sumx = p1.getX() - p2.getX(), sumy = p1.getY() - p2.getY();
		return Math.sqrt(sumx * sumx + sumy * sumy);
	}
	
	public static double distanceFromLineSegment(Point s, Point e, Point p) {
		int sx = s.getX(), sy = s.getY();
		int ex = e.getX(), ey = e.getY();
		int px = p.getX(), py = p.getY();
		if (sx == ex) {		// okomita linija
			if (sy > ey) {
				Point tmp = s;	int i = sy;
				s = e;			sy = ey;
				e = tmp;		ey = i;
			}
			if      (py > ey) { return distanceFromPoint(p, e); }
			else if (py < sy) { return distanceFromPoint(p, s); }
			else              { return Math.abs(px - sx); }
		}
		if (sy == ey) {		// vodoravna linija
			if (sx > ex) {
				Point tmp = s;	int i = sx;
				s = e;			sx = ex;
				e = tmp;		ex = i;
			}
			if		(px > ex) { return distanceFromPoint(p, e); }
			else if (px < sx) { return distanceFromPoint(p, s); }
			else			  { return Math.abs(py - sy); }
		}
		
		// nije okomita ni vodoravna
		double k = (double)(ey - sy) / (ex - sx);
		double ki = -1 / k;
		double x = (k*sx - ki*px + py - sy) / (k - ki);
		double y = k * (x - sx) + sy;
		
		Point inters = new Point((int)x, (int)y);
		double distS = distanceFromPoint(inters, s);
		double distE = distanceFromPoint(inters, e);
		double lineDist = distanceFromPoint(e, s);
		if      (distS > lineDist && distE < distS) { return distanceFromPoint(p, e); }
		else if (distE > lineDist && distE > distS) { return distanceFromPoint(p, s); }
		else										{ return distanceFromPoint(p, inters); }
	}
}
